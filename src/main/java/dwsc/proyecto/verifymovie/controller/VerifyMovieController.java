package dwsc.proyecto.verifymovie.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
public class VerifyMovieController {
	@Value("${apiKey}")
	private String apiKey;

	@Value("${url}")
	private String url;

	@GetMapping("/{title}")
	public String checkMovie(@PathVariable String title) throws Exception, JsonProcessingException {
		// Construct the request
		String encodedTitle;
		try {
			encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException(ex.getCause());
		}

		String completeUrl = url + "?apikey=" + apiKey + "&s=" + encodedTitle;
		
		// Send request and obtain response using Rest Template
		// https://www.baeldung.com/rest-template
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> response = restTemplate.getForEntity(completeUrl, String.class);
		
		//Parse response
		ObjectMapper mapper = new ObjectMapper();
		JsonNode root = mapper.readTree(response.getBody());
		Boolean movieExists= Boolean.parseBoolean(root.path("Response").textValue());
		if (!movieExists) {
			
		} 
			return (root.path("Poster").textValue());
	}
}
