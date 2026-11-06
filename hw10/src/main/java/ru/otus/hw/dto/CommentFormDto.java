package ru.otus.hw.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CommentFormDto {

    @NotBlank(message = "Comment text must not be empty")
    @Size(max = 1000, message = "Comment text too long")
    private String text;
}
