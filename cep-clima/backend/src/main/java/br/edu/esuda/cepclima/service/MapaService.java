package br.edu.esuda.cepclima.service;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

import br.edu.esuda.cepclima.dto.CoordenadasDto;
import br.edu.esuda.cepclima.dto.EnderecoDto;
import br.edu.esuda.cepclima.dto.MapaResponse;

@Service
public class MapaService {

	private static final Logger log = LoggerFactory.getLogger(MapaService.class);

	private final RestClient restClient;

	public MapaService(RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
	}

	public MapaResponse buscarMapaPorCep(String cep) {
		Map<String, Object> viaCep = buscarEnderecoPorCep(cep);

		String logradouro = textoOuVazio(viaCep.get("logradouro"));
		String bairro = textoOuVazio(viaCep.get("bairro"));
		String localidade = (String) viaCep.get("localidade");
		String uf = (String) viaCep.get("uf");
		String cepFormatado = textoOuVazio(viaCep.get("cep"));
		String nomeBusca = String.join(", ",
				logradouro,
				bairro,
				localidade,
				uf,
				cepFormatado);

		try {
			List<Map<String, Object>> resultados = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.scheme("https")
							.host("nominatim.openstreetmap.org")
							.path("/search")
							.queryParam("format", "jsonv2")
							.queryParam("limit", 1)
							.queryParam("countrycodes", "br")
							.queryParam("q", nomeBusca)
							.build())
					.header("User-Agent", "cep-clima-esuda/1.0")
					.retrieve()
					.body(new ParameterizedTypeReference<List<Map<String, Object>>>() {});

			if (resultados == null || resultados.isEmpty()) {
				log.warn("Localidade não encontrada no Nominatim para CEP: {}", cep);
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Localidade não encontrada");
			}

			Map<String, Object> local = resultados.get(0);
			double latitude = Double.parseDouble(local.get("lat").toString());
			double longitude = Double.parseDouble(local.get("lon").toString());
			String nome = textoOuVazio(local.get("display_name"));

			EnderecoDto endereco = new EnderecoDto(logradouro, bairro, localidade, uf);
			CoordenadasDto coordenadas = new CoordenadasDto(latitude, longitude, nome);

			log.info("CEP {} localizado: {}, {} (lat={}, lon={})", cep, localidade, uf, latitude, longitude);

			return new MapaResponse(cepFormatado, endereco, coordenadas);
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RestClientException ex) {
			log.error("Erro ao consultar Nominatim para CEP: {}", cep, ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API de mapa");
		}
	}

	private Map<String, Object> buscarEnderecoPorCep(String cep) {
		String cepLimpo = validarCep(cep);

		try {
			Map<String, Object> viaCep = restClient.get()
					.uri("https://viacep.com.br/ws/{cep}/json/", cepLimpo)
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {});

			if (viaCep == null || Boolean.TRUE.equals(viaCep.get("erro"))) {
				log.warn("CEP não encontrado no ViaCEP: {}", cepLimpo);
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
			}

			return viaCep;
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RestClientException ex) {
			log.error("Erro ao consultar ViaCEP para CEP: {}", cepLimpo, ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API do ViaCEP");
		}
	}

	static String validarCep(String cep) {
		if (cep == null || !cep.matches("\\d{8}|\\d{5}-\\d{3}")) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido");
		}
		return cep.replace("-", "");
	}

	private String textoOuVazio(Object valor) {
		return valor == null ? "" : valor.toString().trim();
	}
}
