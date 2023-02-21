package com.marvel.server.model;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MarvelCharacter implements Serializable {
    private Integer id;
    private String name;
    private String description;
    private String image;
}
