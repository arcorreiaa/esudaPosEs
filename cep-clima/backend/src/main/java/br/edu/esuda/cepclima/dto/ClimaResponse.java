package br.edu.esuda.cepclima.dto;

/**
 * Resposta do endpoint GET /clima/{cep}.
 * Inclui endereço, coordenadas e clima.
 */
public record ClimaResponse(
		String cep,
		EnderecoDto endereco,
		CoordenadasDto coordenadas,
		ClimaDto clima
) {}
