package com.marvel.server.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marvel.server.Utils;
import com.marvel.server.model.MarvelCharacter;
import com.marvel.server.model.MarvelCharacterShort;
import com.marvel.server.service.CommentService;
import com.marvel.server.service.MarvelService;

import jakarta.json.JsonArray;

@RestController
@RequestMapping(path="/api")
// @CrossOrigin(origins = "*")
public class MarvelController {
    
    @Autowired
    private MarvelService marvelSvc;

    @Autowired
    private CommentService commentSvc;


    @GetMapping(path="/characters")
    public ResponseEntity<String> getCharacters(@RequestParam String nameStartsWith, @RequestParam(defaultValue="20") Integer limit,@RequestParam(defaultValue="0") Integer offset){
        System.out.println("IN GET CHARACTERS CONTROLLER");
        System.out.println("Name %s : | Limit : %d | Offset : %d".formatted(nameStartsWith, limit, offset));
        List<MarvelCharacterShort> marvelCharactersShort = marvelSvc.getMarvelCharacters(nameStartsWith, limit, offset);
        if (marvelCharactersShort.isEmpty()){
            String responseBody = Utils.createResponseObject("count", "0").toString();
            return ResponseEntity.ok(responseBody);
        }
        try {
            //using Jackson to serialize the POJO
            ObjectMapper objMapper = new ObjectMapper();
            String responseBody = objMapper.writeValueAsString(marvelCharactersShort);
            return ResponseEntity.ok(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            String errorResponse = Utils.createResponseObject("error", e.getMessage()).toString();
            return ResponseEntity.status(500).body(errorResponse);
        }

    }

    @GetMapping(path="/character/{characterId}")
    public ResponseEntity<String> getCharacterById(@PathVariable("characterId") Integer id){
        System.out.println("IN GET CHARACTER CONTROLLER. ID %d".formatted(id));
        MarvelCharacter marvelChar = marvelSvc.getCharacter(id);
        try {
            ObjectMapper objMapper = new ObjectMapper();
            return ResponseEntity.ok(objMapper.writeValueAsString(marvelChar));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            String errorResponse = Utils.createResponseObject("error", e.getMessage()).toString();
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    @PostMapping(path="/character/{characterId}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveCharacterComment(@PathVariable("characterId") Integer id, @RequestBody Map<String,String> reqBody){
        System.out.println("IN POST COMMENT CONTROLLER");
        System.out.println("ID %d".formatted(id));
        System.out.println("REQUEST BODY " + reqBody);
        commentSvc.saveComment(id, reqBody);
        String response = Utils.createResponseObject("success", "comment added").toString();
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping(path="/character/{characterId}/comments")
    public ResponseEntity<String> getCharacterComments(@PathVariable("characterId") Integer id){
        System.out.println(" IN GET ALL COMMENTS CONTROLLER");
        System.out.println("ID %d".formatted(id));
        List<String> comments = commentSvc.getComments(id);
        JsonArray commentsJson = Utils.createJsonArray(comments);
        String responseBody = Utils.createResponseObject("comments", commentsJson).toString();
        return ResponseEntity.ok(responseBody);
    }
}
