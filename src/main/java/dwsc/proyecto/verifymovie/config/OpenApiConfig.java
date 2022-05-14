package dwsc.proyecto.verifymovie.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {
	@Value("${titulo}")
	String titulo;
	@Value("${descripcion}")
	String descripcion;
	@Value("${autor}")
	String autor;
	@Value("${site}")
	String site;
	@Value("${correo}")
	String correo;
	@Value("${version}")
	String version;

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().components(new Components()).info(new Info().title(titulo).description(descripcion)
				.contact(new Contact().name(autor).url(site).email(correo)).version(version));
	}
}
