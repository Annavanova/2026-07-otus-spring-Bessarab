package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

@Service
@RequiredArgsConstructor
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    public Flux<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Mono<Author> findById(String id) {
        return authorRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Author with id %s not found".formatted(id))));
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return bookRepository.existsByAuthorId(id)
                .flatMap(exists -> exists
                        ? Mono.error(new EntityNotFoundException(
                        "Cannot delete author %s: books exist".formatted(id)))
                        : authorRepository.deleteById(id));
    }
}