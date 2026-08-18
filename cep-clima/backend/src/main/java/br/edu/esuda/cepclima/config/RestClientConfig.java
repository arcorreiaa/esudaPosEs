package br.edu.esuda.cepclima.config;

import java.time.Duration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

/**
 * Configuração centralizada do RestClient com timeouts
 * para evitar que chamadas a APIs externas travem indefinidamente.
 */
@Configuration
public class RestClientConfig {

	@Bean
	public RestClient.Builder restClientBuilder() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(Duration.ofSeconds(5));
		factory.setReadTimeout(Duration.ofSeconds(10));

		return RestClient.builder().requestFactory(factory);
	}
}
