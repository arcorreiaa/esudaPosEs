package br.edu.esuda.cepclima.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.edu.esuda.cepclima.dto.MapaResponse;
import br.edu.esuda.cepclima.service.MapaService;

@RestController
public class MapaController {

	private final MapaService mapaService;

	public MapaController(MapaService mapaService) {
		this.mapaService = mapaService;
	}

	@GetMapping("/mapa/{cep}")
	public MapaResponse buscarMapa(@PathVariable String cep) {
		return mapaService.buscarMapaPorCep(cep);
	}
}
