package com.marvel.server.repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.marvel.server.model.MarvelCharacter;

@Repository
public class CharacterRepository {
    
    @Autowired
    private RedisTemplate<String,Object> redisTemp;

    public void saveMarvelCharacters(List<MarvelCharacter> chars){
        chars.forEach(character -> {
            //set expiry 1hr
            redisTemp.opsForValue().set(character.getId().toString(), character, Duration.ofHours(1));
        });
    }

    public Optional<MarvelCharacter> getMarvelCharacter(Integer id){
        return Optional.ofNullable((MarvelCharacter)redisTemp.opsForValue().get(id.toString()));
    }

    public void saveCharacter(MarvelCharacter marvelChar){
        redisTemp.opsForValue().set(marvelChar.getId().toString(), marvelChar, Duration.ofHours(1));
    }


}
