package ru.otus.hw.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class BookFormDto {

    @NotBlank(message = "Title must not be empty")
    private String title;

    @NotBlank(message = "Author id must not be empty")
    private String authorId;

    @NotEmpty(message = "At least one genre must be selected")
    private Set<String> genreIds = new HashSet<>();
}
