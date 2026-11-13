package ru.otus.hw.repositories;

import reactor.core.publisher.Mono;

public interface BookRepositoryCustom {
    Mono<Boolean> existsByAuthorId(String authorId);
}