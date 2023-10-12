package com.bank.system;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Bank Backend App",
				description = "A backend Rest Api for a bank simulation",
				version = "v1.0",
				contact =  @Contact(
						name = "Oussama",
						email = "oussamaait2001@gmail.com",
						url = "https://oussamaaitlamaalam.com"
				),
				license = @License(
						name = "OSM",
						url = "https://oussamaaitlamaalam.com"

				)

		),
		externalDocs = @ExternalDocumentation(
				description = "A backend Rest Api for a bank simulation",
				url = "https://oussamaaitlamaalam.com"

		)
)
public class SystemApplication {

	public static void main(String[] args) {
		SpringApplication.run(SystemApplication.class, args);
	}

}
