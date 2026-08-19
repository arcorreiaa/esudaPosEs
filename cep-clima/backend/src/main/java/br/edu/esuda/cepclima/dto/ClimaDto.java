package br.edu.esuda.cepclima.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dados de clima (temperatura máxima do dia).
 */
public record ClimaDto(
		String data,
		@JsonProperty("temperatura_maxima_celsius") double temperaturaMaximaCelsius
) {}
