package com.marvel.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.marvel.server.model.MarvelCharacter;
import com.marvel.server.service.MarvelService;

@SpringBootApplication
public class ServerApplication {

	public static void main(String[] args)  {
		SpringApplication.run(ServerApplication.class, args);
	}



}
