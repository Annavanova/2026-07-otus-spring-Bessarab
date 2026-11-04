package ru.otus.hw.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({AuthorServiceImpl.class, BookServiceImpl.class, CommentServiceImpl.class,
        GenreServiceImpl.class})
@DisplayName("AuthorService (MongoDB)")
public class AuthorServiceTest {
    @Autowired
    private AuthorService authorService;
    @Autowired private BookService bookService;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();
    }

    @Test
    @DisplayName("findAll должен возвращать всех авторов")
    void findAll_shouldReturnAll() {
        authorRepository.save(new Author(null, "A1"));
        authorRepository.save(new Author(null, "A2"));

        assertThat(authorService.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("findById должен возвращать автора")
    void findById_shouldReturnAuthor() {
        var author = authorRepository.save(new Author(null, "A"));
        assertThat(authorService.findById(author.getId()).getFullName()).isEqualTo("A");
    }

    @Test
    @DisplayName("findById с несуществующим id должен бросать EntityNotFoundException")
    void findById_unknown_shouldThrow() {
        assertThatThrownBy(() -> authorService.findById("000000000000000000000000"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteById без книг должен удалять автора")
    void deleteById_withoutBooks_shouldDelete() {
        var author = authorRepository.save(new Author(null, "A"));
        authorService.deleteById(author.getId());
        assertThat(authorRepository.findById(author.getId())).isEmpty();
    }

    @Test
    @DisplayName("deleteById с книгами должен бросать EntityNotFoundException")
    void deleteById_withBooks_shouldThrow() {
        var author = authorRepository.save(new Author(null, "A"));
        var genre = genreRepository.save(new Genre(null, "G"));
        bookService.insert("Book", author.getId(), Set.of(genre.getId()));

        assertThatThrownBy(() -> authorService.deleteById(author.getId()))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("books exist");
    }
}
