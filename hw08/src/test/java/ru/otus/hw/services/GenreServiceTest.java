package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Import(GenreServiceImpl.class)
@DisplayName("GenreService (MongoDB)")
public class GenreServiceTest {

    @Autowired private GenreService genreService;
    @Autowired private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("findAll должен возвращать все жанры")
    void findAll_shouldReturnAll() {
        genreRepository.save(new Genre(null, "G1"));
        genreRepository.save(new Genre(null, "G2"));

        assertThat(genreService.findAll())
                .hasSize(2)
                .extracting(Genre::getName)
                .containsExactlyInAnyOrder("G1", "G2");
    }

    @Test
    @DisplayName("findAll для пустой коллекции должен возвращать пустой список")
    void findAll_empty_shouldReturnEmpty() {
        assertThat(genreService.findAll()).isEmpty();
    }
}
