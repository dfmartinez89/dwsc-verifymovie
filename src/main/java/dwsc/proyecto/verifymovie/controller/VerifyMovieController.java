package dwsc.proyecto.verifymovie.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import dwsc.proyecto.verifymovie.exceptions.CustomResponse;
import dwsc.proyecto.verifymovie.exceptions.MovieNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "movies", description = "validate movie")
public class VerifyMovieController {
	@Value("${apiKey}")
	private String apiKey;

	@Value("${url}")
	private String url;

	private static final Gson gson = new Gson();

	@Operation(summary = "Find movie by title and year", description = "Operation to validate the existence of a movie given its title and optionally its year")
	@ApiResponses({ @ApiResponse(responseCode = "200", description = "movie is  valid"),
			@ApiResponse(responseCode = "404", description = "movie not found", content = @Content(schema = @Schema(implementation = CustomResponse.class))) })
	@RequestMapping(method = RequestMethod.GET, path = "/{title}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkMovie(@Parameter(description = "Movie title") @PathVariable String title,
			@Parameter(description = "Movie year") @RequestParam(required = false) Integer year)
			throws Exception, JsonProcessingException {
		// Construct the request
		String encodedTitle;
		try {
			encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}

		String completeUrl = url + "?apikey=" + apiKey + "&t=" + encodedTitle + "&y=" + year+"";

		// Send request and obtain response using Rest Template
		// https://www.baeldung.com/rest-template
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(completeUrl, String.class);

		// Parse response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.getBody());
		Boolean movieExists = Boolean.parseBoolean(root.path("Response").textValue());
		if (!movieExists) {
			throw new MovieNotFoundException(HttpStatus.NOT_FOUND, "The movie with title: " + title + " does not exist");
		}
		String posterUrl = (root.path("Poster").textValue());

		return ResponseEntity.ok(gson.toJson(posterUrl)); // string wrapped in json

	}
}
