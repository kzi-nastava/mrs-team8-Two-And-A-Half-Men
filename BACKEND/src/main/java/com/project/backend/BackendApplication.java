package com.project.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

	@RequestMapping(value = "/**/{path:[^\\.]*}", produces = MediaType.TEXT_HTML_VALUE)
	public String notFound() {
		return "<h1>404 Not found</h1> <a href='/'>Back home</a>";

	}
}
