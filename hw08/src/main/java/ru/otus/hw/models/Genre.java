package ru.otus.hw.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "genres")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Genre {

    @Id
    private String id;

    private String name;

    @Override
    public String toString() {
        return "Genre{id='%s', name='%s'}".formatted(id, name);
    }
}
