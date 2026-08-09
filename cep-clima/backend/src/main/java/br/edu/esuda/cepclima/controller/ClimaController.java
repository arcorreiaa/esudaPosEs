package br.edu.esuda.cepclima.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.edu.esuda.cepclima.service.ClimaService;

@RestController
public class ClimaController {

	private final ClimaService climaService;

	public ClimaController(ClimaService climaService) {
		this.climaService = climaService;
	}

	@GetMapping("/clima/{cep}")
	public Map<String, Object> buscarClima(@PathVariable String cep) {
		return climaService.buscarClimaPorCep(cep);
	}
}
