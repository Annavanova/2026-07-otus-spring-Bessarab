package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final CommentRepository commentRepository;

    @Override
    public Flux<Book> findAll() {
        return bookRepository.findAll().flatMap(this::enrichBook);
    }

    @Override
    public Mono<Book> findById(String id) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %s not found".formatted(id))))
                .flatMap(this::enrichBook);
    }

    @Override
    public Mono<Book> insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    public Mono<Book> update(String id, String title, String authorId, Set<String> genresIds) {
        return bookRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %s not found".formatted(id))))
                .flatMap(existing -> save(id, title, authorId, genresIds));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteAllByBookId(id)
                .then(bookRepository.deleteById(id));
    }

    private Mono<Book> save(String id, String title, String authorId, Set<String> genresIds) {
        if (genresIds == null || genresIds.isEmpty()) {
            return Mono.error(new IllegalArgumentException("Genres ids must not be null"));
        }
        if (authorId == null || authorId.isBlank()) {
            return Mono.error(new IllegalArgumentException("Author id must not be null"));
        }
        var genreIdsList = List.copyOf(genresIds);
        return Mono.zip(
                        authorRepository.findById(authorId)
                                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                                        "Author with id %s not found".formatted(authorId)))),
                        genreRepository.findAllByIdIn(genreIdsList).collectList())
                .flatMap(tuple -> {
                    Author author = tuple.getT1();
                    List<Genre> genres = tuple.getT2();
                    if (genres.size() != genreIdsList.size()) {
                        return Mono.error(new EntityNotFoundException(
                                "One or all genres with ids %s not found".formatted(genresIds)));
                    }
                    var book = new Book(id, title, authorId, genreIdsList);
                    return bookRepository.save(book)
                            .map(saved -> new Book(saved.getId(), saved.getTitle(), author, genres));
                });
    }

    private Mono<Book> enrichBook(Book book) {
        if (book == null) {
            return Mono.empty();
        }
        String authorId = book.getAuthorId();
        List<String> genreIds = book.getGenreIds() != null ? book.getGenreIds() : List.of();

        Mono<Author> authorMono = authorId != null
                ? authorRepository.findById(authorId)
                .defaultIfEmpty(new Author(authorId, "Unknown"))
                : Mono.just(new Author(null, "Unknown"));

        Mono<List<Genre>> genresMono = Flux.fromIterable(genreIds)
                .flatMap((String id) -> genreRepository.findById(id))
                .collectList();

        return Mono.zip(authorMono, genresMono)
                .map(tuple -> new Book(book.getId(), book.getTitle(), tuple.getT1(), tuple.getT2()));
    }
}