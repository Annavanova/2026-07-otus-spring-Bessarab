package ru.otus.hw.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "comments")
@Getter
@Setter
@NoArgsConstructor
public class Comment {

    @Id
    private String id;

    private String text;

    private String bookId;

    @Transient
    private Book book;

    public Comment(String id, String text, String bookId) {
        this.id = id;
        this.text = text;
        this.bookId = bookId;
    }

    public Comment(String id, String text, Book book) {
        this.id = id;
        this.text = text;
        this.book = book;
        this.bookId = book != null ? book.getId() : null;
    }

    @Override
    public String toString() {
        return "Comment{id='%s', text='%s'}".formatted(id, text);
    }
}