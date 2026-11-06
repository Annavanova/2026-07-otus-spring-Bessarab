package ru.otus.hw.repositories;

public interface BookRepositoryCustom {
    boolean existsByAuthorId(String authorId);
}
