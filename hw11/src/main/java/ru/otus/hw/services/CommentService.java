package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentService {
    Mono<Comment> findById(String id);
    Flux<Comment> findAllByBookId(String bookId);
    Mono<Comment> insert(String text, String bookId);
    Mono<Comment> update(String id, String text);
    Mono<Void> deleteById(String id);
    Mono<Void> deleteByIdAndBookId(String id, String bookId);
}
