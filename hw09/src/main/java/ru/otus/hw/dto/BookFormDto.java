package ru.otus.hw.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class BookFormDto {
    private String id;
    private String title;
    private String authorId;
    private Set<String> genreIds = new HashSet<>();
}
