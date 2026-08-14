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
	private final MapaService mapaService;

	public ClimaService(MapaService mapaService) {
		this.restClient = RestClient.create();
		this.mapaService = mapaService;
	}

	public Map<String, Object> buscarClimaPorCep(String cep) {
		try {
			Map<String, Object> mapa = mapaService.buscarMapaPorCep(cep);
			Map<String, Object> coordenadas = (Map<String, Object>) mapa.get("coordenadas");

			double latitude = ((Number) coordenadas.get("latitude")).doubleValue();
			double longitude = ((Number) coordenadas.get("longitude")).doubleValue();

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

			Map<String, Object> clima = new LinkedHashMap<>();
			clima.put("data", datas.get(0));
			clima.put("temperatura_maxima_celsius", temperaturas.get(0).doubleValue());

			Map<String, Object> resposta = new LinkedHashMap<>(mapa);
			resposta.put("clima", clima);

			return resposta;
		} catch (ResponseStatusException ex) {
			throw ex;
		} catch (RestClientException ex) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API de clima");
		}
	}

	private Map<String, Object> extrairDaily(Map<String, Object> forecast) {
		if (forecast == null || forecast.get("daily") == null) {
			throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Erro ao consultar API de clima");
		}
		return (Map<String, Object>) forecast.get("daily");
	}
}
