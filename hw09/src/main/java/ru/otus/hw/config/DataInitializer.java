package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(String... args) {
        // Инициализируем только если коллекции пусты
        if (authorRepository.count() > 0) {
            return;
        }

        var a1 = authorRepository.save(new Author(null, "Author_1"));
        var a2 = authorRepository.save(new Author(null, "Author_2"));
        var a3 = authorRepository.save(new Author(null, "Author_3"));

        var g1 = genreRepository.save(new Genre(null, "Genre_1"));
        var g2 = genreRepository.save(new Genre(null, "Genre_2"));
        var g3 = genreRepository.save(new Genre(null, "Genre_3"));
        var g4 = genreRepository.save(new Genre(null, "Genre_4"));
        var g5 = genreRepository.save(new Genre(null, "Genre_5"));
        var g6 = genreRepository.save(new Genre(null, "Genre_6"));

        var b1 = bookRepository.save(new Book(null, "BookTitle_1", a1, List.of(g1, g2)));
        var b2 = bookRepository.save(new Book(null, "BookTitle_2", a2, List.of(g3, g4)));
        var b3 = bookRepository.save(new Book(null, "BookTitle_3", a3, List.of(g5, g6)));

        commentRepository.save(new Comment(null, "Great book!", b1));
        commentRepository.save(new Comment(null, "Must read", b1));
        commentRepository.save(new Comment(null, "Interesting", b2));
    }
}
