package com.rccl.otel.demos.standalone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rccl.otel.demos.standalone.config.EnvironmentConfig;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class CartMicroservice {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Bean
	public ObjectMapper objectMapper() {
		return OBJECT_MAPPER;
	}

	@Bean(name="webClient")
	public WebClient webClient(@Autowired EnvironmentConfig config) {
		return WebClient.builder()
				.baseUrl(config.inventoryBaseUrl)
				.defaultHeader("REQUESTED-BY", config.getServiceName())
				.build();
	}

//	@Bean
//	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
//
//		// Todo: log event
//		System.out.println("Initializing H2 in-memory database using schema.sql.");
//		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
//		initializer.setConnectionFactory(connectionFactory);
//		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));
//
//		return initializer;
//	}

	public static void main(String[] args) {
		System.out.println("\n\nXXXX cart-svc is running ####\n\n");
		var app = new SpringApplication(CartMicroservice.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

}
