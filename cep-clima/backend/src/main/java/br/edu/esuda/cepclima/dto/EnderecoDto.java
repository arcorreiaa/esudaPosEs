package br.edu.esuda.cepclima.dto;

/**
 * Dados de endereço retornados pela API.
 */
public record EnderecoDto(
		String logradouro,
		String bairro,
		String localidade,
		String uf
) {}
