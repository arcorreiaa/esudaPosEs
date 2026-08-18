package br.edu.esuda.cepclima.dto;

/**
 * Coordenadas geográficas obtidas via Nominatim.
 */
public record CoordenadasDto(
		double latitude,
		double longitude,
		String nome
) {}
