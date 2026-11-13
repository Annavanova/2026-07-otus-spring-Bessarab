package ru.otus.hw.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "books")
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    private String id;

    private String title;

    private String authorId;

    private List<String> genreIds;

    @Transient
    private Author author;

    @Transient
    private List<Genre> genres;

    public Book(String id, String title, String authorId, List<String> genreIds) {
        this.id = id;
        this.title = title;
        this.authorId = authorId;
        this.genreIds = genreIds;
    }

    public Book(String id, String title, Author author, List<Genre> genres) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genres = genres;
        this.authorId = author != null ? author.getId() : null;
        this.genreIds = genres != null ? genres.stream().map(Genre::getId).toList() : List.of();
    }

    @Override
    public String toString() {
        return "Book{id='%s', title='%s'}".formatted(id, title);
    }
}