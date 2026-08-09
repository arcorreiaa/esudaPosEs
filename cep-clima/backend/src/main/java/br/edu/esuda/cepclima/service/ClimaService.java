package br.edu.esuda.cepclima.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ClimaService {

	private final RestClient restClient;

	public ClimaService() {
		this.restClient = RestClient.create();
	}

	public Map<String, Object> buscarClimaPorCep(String cep) {
		String cepLimpo = cep.replaceAll("\\D", "");
		if (cepLimpo.length() != 8) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CEP inválido");
		}

		try {
			Map<String, Object> viaCep = restClient.get()
					.uri("https://viacep.com.br/ws/{cep}/json/", cepLimpo)
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {});

			if (viaCep == null || Boolean.TRUE.equals(viaCep.get("erro"))) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "CEP não encontrado");
			}

			String localidade = (String) viaCep.get("localidade");
			String uf = (String) viaCep.get("uf");
			String nomeBusca = localidade + ", " + uf;

			Map<String, Object> geoResponse = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.scheme("https")
							.host("geocoding-api.open-meteo.com")
							.path("/v1/search")
							.queryParam("name", nomeBusca)
							.queryParam("count", 1)
							.queryParam("language", "pt")
							.queryParam("countryCode", "BR")
							.build())
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {});

			List<Map<String, Object>> results = extrairResultadosGeocoding(geoResponse);
			if (results.isEmpty()) {
				throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Localidade não encontrada");
			}

			Map<String, Object> local = results.get(0);
			double latitude = ((Number) local.get("latitude")).doubleValue();
			double longitude = ((Number) local.get("longitude")).doubleValue();
			String nome = (String) local.get("name");

			Map<String, Object> forecast = restClient.get()
					.uri(uriBuilder -> uriBuilder
							.scheme("https")
							.host("api.open-meteo.com")
							.path("/v1/forecast")
							.queryParam("latitude", latitude)
							.queryParam("longitude", longitude)
							.queryParam("daily", "temperature_2m_max")
							.queryParam("forecast_days", 1)
							.queryParam("timezone", "auto")
							.build())
					.retrieve()
					.body(new ParameterizedTypeReference<Map<String, Object>>() {});

			Map<String, Object> daily = extrairDaily(forecast);
			List<String> datas = (List<String>) daily.get("time");
			List<Number> temperaturas = (List<Number>) daily.get("temperature_2m_max");

			Map<String, Object> endereco = new LinkedHashMap<>();
			endereco.put("logradouro", viaCep.get("logradouro"));
			endereco.put("bairro", viaCep.get("bairro"));
			endereco.put("localidade", localidade);
			endereco.put("uf", uf);

			Map<String, Object> coordenadas = new LinkedHashMap<>();
			coordenadas.put("latitude", latitude);
			coordenadas.put("longitude", longitude);
			coordenadas.put("nome", nome);

			Map<String, Object> clima = new LinkedHashMap<>();
			clima.put("data", datas.get(0));
			clima.put("temperatura_maxima_celsius", temperaturas.get(0).doubleValue());

			Map<String, Object> resposta = new LinkedHashMap<>();
			resposta.put("cep", viaCep.get("cep"));
			resposta.put("endereco", endereco);
			resposta.put("coordenadas", coordenadas);
			resposta.put("clima", clima);

			return resposta;
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar serviço externo");
		}
	}

	private List<Map<String, Object>> extrairResultadosGeocoding(Map<String, Object> geoResponse) {
		if (geoResponse == null || geoResponse.get("results") == null) {
			return List.of();
		}
		return (List<Map<String, Object>>) geoResponse.get("results");
	}

	private Map<String, Object> extrairDaily(Map<String, Object> forecast) {
		if (forecast == null || forecast.get("daily") == null) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar serviço externo");
		}
		return (Map<String, Object>) forecast.get("daily");
	}
}
