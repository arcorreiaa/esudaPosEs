package br.edu.esuda.demospring01;

import java.util.Arrays;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class Demospring01Application {
	public static void main(String[] args) {
		SpringApplication.run(Demospring01Application.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("Aqui vamos imprimir no console todos os Java Beans");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			System.out.println("-*-*-*-*-*-*-*-*-*-*-*-*-*-*");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}
}