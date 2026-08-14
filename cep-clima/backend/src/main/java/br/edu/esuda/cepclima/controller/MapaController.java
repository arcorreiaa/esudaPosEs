package br.edu.esuda.cepclima.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import br.edu.esuda.cepclima.service.MapaService;

@RestController
public class MapaController {

	private final MapaService mapaService;

	public MapaController(MapaService mapaService) {
		this.mapaService = mapaService;
	}

	@GetMapping("/mapa/{cep}")
	public Map<String, Object> buscarMapa(@PathVariable String cep) {
		return mapaService.buscarMapaPorCep(cep);
	}
}
