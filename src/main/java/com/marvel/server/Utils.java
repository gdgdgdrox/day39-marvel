package com.marvel.server;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;


import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;

public class Utils {
    
    public static String generateHash(Long ts, String publicKey, String privateKey){
        String signature = "%d%s%s".formatted(ts, privateKey, publicKey);
        String hash = "";
        try {
            // Message digest = md5, sha1, sha512
            // Get an instance of MD5
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            // Calculate our hash
            // Update our message digest
            md5.update(signature.getBytes());
            // Get the MD5 digest
            byte[] h = md5.digest();
            // Stringify the MD5 digest
            hash = HexFormat.of().formatHex(h);
        } catch (Exception ex) { }
        return hash;
    }

    public static JsonObject processPayloadFromMarvelAPI(String payload){
        StringReader sr = new StringReader(payload);
        JsonReader jr = Json.createReader(sr);
        return jr.readObject();
    }

    public static JsonObject createResponseObject(String key, String message){
        return Json.createObjectBuilder().add(key, message).build();
    }

    public static JsonObject createResponseObject(String key, JsonArray message){
        return Json.createObjectBuilder().add(key, message).build();
    }

    public static JsonArray createJsonArray(List<String> message){
        JsonArrayBuilder jab = Json.createArrayBuilder();
        message.forEach(m -> jab.add(m));
        return jab.build();
    }
}
