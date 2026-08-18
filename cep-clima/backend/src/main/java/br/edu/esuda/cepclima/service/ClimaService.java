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

import br.edu.esuda.cepclima.dto.ClimaDto;
import br.edu.esuda.cepclima.dto.ClimaResponse;
import br.edu.esuda.cepclima.dto.MapaResponse;

@Service
public class ClimaService {

	private static final Logger log = LoggerFactory.getLogger(ClimaService.class);

	private final RestClient restClient;
	private final MapaService mapaService;

	public ClimaService(MapaService mapaService, RestClient.Builder restClientBuilder) {
		this.restClient = restClientBuilder.build();
		this.mapaService = mapaService;
	}

	public ClimaResponse buscarClimaPorCep(String cep) {
		try {
			MapaResponse mapa = mapaService.buscarMapaPorCep(cep);

			double latitude = mapa.coordenadas().latitude();
			double longitude = mapa.coordenadas().longitude();

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

			ClimaDto clima = new ClimaDto(datas.get(0), temperaturas.get(0).doubleValue());

			log.info("Clima consultado para CEP {}: {} °C em {}", cep, clima.temperaturaMaximaCelsius(), clima.data());

			return new ClimaResponse(mapa.cep(), mapa.endereco(), mapa.coordenadas(), clima);
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RestClientException ex) {
			log.error("Erro ao consultar API de clima para CEP: {}", cep, ex);
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API de clima");
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> extrairDaily(Map<String, Object> forecast) {
		if (forecast == null || forecast.get("daily") == null) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API de clima");
		}
		return (Map<String, Object>) forecast.get("daily");
	}
}
