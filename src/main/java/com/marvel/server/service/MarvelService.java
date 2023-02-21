package com.marvel.server.service;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.marvel.server.Utils;
import com.marvel.server.model.MarvelCharacter;
import com.marvel.server.model.MarvelCharacterShort;
import com.marvel.server.repository.CharacterRepository;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;

@Service
public class MarvelService {

    @Value("${marvel.public.api.key}")
    private String publicKey; 

    @Value("${marvel.private.api.key}")
    private String privateKey; 

    @Autowired
    private CharacterRepository charRepo;

    private static String baseUrl = "https://gateway.marvel.com/v1/public/characters";
    public RestTemplate restTemplate = new RestTemplate();

    public List<MarvelCharacterShort> getMarvelCharacters(String nameStartsWith, Integer limit, Integer offset){
        Long ts = System.currentTimeMillis();
        String hash = Utils.generateHash(ts, publicKey,privateKey);

        /*
*     //https://gateway.marvel.com/v1/public/characters?nameStartsWith=th&limit=10&offset=5&apikey=d1943aadb34c3f9f7d4a83d8da7c1d57
    //&hash=ffd275c5130566a2916217b101f26150
         */
        String fullUrl = UriComponentsBuilder.fromUriString(baseUrl).queryParam("nameStartsWith", nameStartsWith)
                                                    .queryParam("limit", limit)
                                                    .queryParam("offset", offset)
                                                    .queryParam("ts", ts)
                                                    .queryParam("apikey", publicKey)
                                                    .queryParam("hash", hash)
                                                    .toUriString();
        System.out.println("FULL URL %s".formatted(fullUrl));

        System.out.println("CALLING MARVEL API FOR CHARACTERS THAT STARTS WITH : %s".formatted(nameStartsWith));
        ResponseEntity<String> respEntity = restTemplate.getForEntity(fullUrl, String.class);

        //process the payload
        JsonObject payload = Utils.processPayloadFromMarvelAPI(respEntity.getBody());
        JsonArray jsonArray = payload.getJsonObject("data").getJsonArray("results");

        //return this to client so as to populate View 1 (list of character name)
        List<MarvelCharacterShort> marvelCharactersShort = jsonArray.stream().map(obj -> 
            new MarvelCharacterShort(obj.asJsonObject().getInt("id"), obj.asJsonObject().getString("name"))
        ).toList();
        // marvelCharactersShort.forEach(o -> System.out.println(o));


        //get character info for saving to Redis
        List<MarvelCharacter> marvelCharacters = jsonArray.stream().map(obj -> toMarvelCharacter(obj.asJsonObject())).toList();

        //save to Redis each character's info
        System.out.println("SAVING MARVEL CHARACTERS TO REDIS FOR");
        charRepo.saveMarvelCharacters(marvelCharacters);
        
        return marvelCharactersShort;
    }


    public MarvelCharacter getCharacter(Integer id){
        Optional<MarvelCharacter> optChar = charRepo.getMarvelCharacter(id);
        if (optChar.isEmpty()){
            //get character from API
            System.out.println("Unable to find character %d in Redis".formatted(id));
            Long ts = System.currentTimeMillis();
            String hash = Utils.generateHash(ts, publicKey, privateKey);
            String fullUrl = UriComponentsBuilder.fromUriString(baseUrl + "/" + id.toString())
                                                .queryParam("ts", ts)
                                                .queryParam("apikey", publicKey)
                                                .queryParam("hash", hash).toUriString();
            System.out.println("FULL URL %s".formatted(fullUrl));
            System.out.println("CALLING MARVEL API FOR CHARACTER ID %d".formatted(id));
            ResponseEntity<String> respEntity = restTemplate.getForEntity(fullUrl, String.class);
            JsonObject payload = Utils.processPayloadFromMarvelAPI(respEntity.getBody());
            JsonArray jsonArray = payload.getJsonObject("data").getJsonArray("results");
            MarvelCharacter marvelChar = toMarvelCharacter(jsonArray.get(0).asJsonObject());
            System.out.println("MARVEL CHAR\n" + marvelChar);

            //save to Redis
            System.out.println("SAVING TO REDIS");
            return marvelChar;
        }
        else{
            System.out.println("Retrieving character %d from Redis".formatted(id));
            return optChar.get();
        }
    }

    public static MarvelCharacter toMarvelCharacter(JsonObject data){
        MarvelCharacter mvChar = new MarvelCharacter();
        mvChar.setId(data.getInt("id"));
        mvChar.setName(data.getString("name"));
        mvChar.setDescription(data.getString("description"));
        mvChar.setImage( "%s.%s".formatted(data.getJsonObject("thumbnail").getString("path"),data.getJsonObject("thumbnail").getString("extension")));
        return mvChar;
    }





}
