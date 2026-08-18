package br.edu.esuda.cepclima.dto;

/**
 * Resposta do endpoint GET /mapa/{cep}.
 */
public record MapaResponse(
		String cep,
		EnderecoDto endereco,
		CoordenadasDto coordenadas
) {}
