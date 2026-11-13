package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
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
public class DataInitializer implements ApplicationRunner {

    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final CommentRepository commentRepository;

    @Override
    public void run(ApplicationArguments args) {
        authorRepository.count()
                .filter(count -> count == 0)
                .flatMap(count -> initData())
                .block();
    }

    private Mono<Void> initData() {
        return Mono.zip(
                        authorRepository.save(new Author(null, "Author_1")),
                        authorRepository.save(new Author(null, "Author_2")),
                        authorRepository.save(new Author(null, "Author_3")))
                .flatMap(authors -> Mono.zip(
                                genreRepository.save(new Genre(null, "Genre_1")),
                                genreRepository.save(new Genre(null, "Genre_2")),
                                genreRepository.save(new Genre(null, "Genre_3")),
                                genreRepository.save(new Genre(null, "Genre_4")),
                                genreRepository.save(new Genre(null, "Genre_5")),
                                genreRepository.save(new Genre(null, "Genre_6")))
                        .flatMap(genres -> {
                            var a1 = authors.getT1(); var a2 = authors.getT2(); var a3 = authors.getT3();
                            var g1 = genres.getT1(); var g2 = genres.getT2(); var g3 = genres.getT3();
                            var g4 = genres.getT4(); var g5 = genres.getT5(); var g6 = genres.getT6();

                            return Mono.zip(
                                            bookRepository.save(new Book(null, "BookTitle_1", a1.getId(), List.of(g1.getId(), g2.getId()))),
                                            bookRepository.save(new Book(null, "BookTitle_2", a2.getId(), List.of(g3.getId(), g4.getId()))),
                                            bookRepository.save(new Book(null, "BookTitle_3", a3.getId(), List.of(g5.getId(), g6.getId()))))
                                    .flatMap(books -> {
                                        var b1 = books.getT1(); var b2 = books.getT2();
                                        return Mono.zip(
                                                        commentRepository.save(new Comment(null, "Great book!", b1.getId())),
                                                        commentRepository.save(new Comment(null, "Must read", b1.getId())),
                                                        commentRepository.save(new Comment(null, "Interesting", b2.getId())))
                                                .then();
                                    });
                        }));
    }
}