package br.edu.esuda.cepclima.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

class MapaServiceTest {

	@ParameterizedTest
	@ValueSource(strings = { "50050480", "50050-480" })
	void deveAceitarCepComOuSemHifen(String cep) {
		assertEquals("50050480", MapaService.validarCep(cep));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"",
			"123",
			"abc50050480xyz",
			"50050_480",
			"50050--480",
			"50050480abc"
	})
	void deveRejeitarFormatoInvalido(String cep) {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> MapaService.validarCep(cep));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("CEP inválido", exception.getReason());
	}

	@Test
	void deveRejeitarCepNulo() {
		ResponseStatusException exception = assertThrows(
				ResponseStatusException.class,
				() -> MapaService.validarCep(null));

		assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
		assertEquals("CEP inválido", exception.getReason());
	}
}
