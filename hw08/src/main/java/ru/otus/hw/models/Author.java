package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "authors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Author {
    @Id
    private String id;

    private String fullName;

    @Override
    public String toString() {
        return "Author{id='%s', fullName='%s'}".formatted(id, fullName);
    }
}
