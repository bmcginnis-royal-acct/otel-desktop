package com.rccl.otel.demos.standalone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rccl.otel.demos.standalone.config.EnvironmentConfig;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@SpringBootApplication
public class TutorialMicroservice {
	public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	@Bean
	public ObjectMapper objectMapper() {
		return OBJECT_MAPPER;
	}

	@Bean
	ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {

		// Todo: log event
		log.info("Initializing H2 in-memory database using schema.sql.");
		ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
		initializer.setConnectionFactory(connectionFactory);
		initializer.setDatabasePopulator(new ResourceDatabasePopulator(new ClassPathResource("schema.sql")));

		return initializer;
	}

	@Bean
	public OpenTelemetry openTelemetry() {
		return GlobalOpenTelemetry.get();
	}

	public static void main(String[] args) {
		var app = new SpringApplication(TutorialMicroservice.class);
		app.setBannerMode(Mode.OFF);
		app.run(args);
	}

}
