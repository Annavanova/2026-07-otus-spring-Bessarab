package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.GenreRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Set;

@DataMongoTest
@Import({BookServiceImpl.class, CommentServiceImpl.class,
        AuthorServiceImpl.class, GenreServiceImpl.class})
@DisplayName("BookService (MongoDB)")
public class BookServiceTest {

    @Autowired
    private BookService bookService;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private CommentRepository commentRepository;

    private Author author;
    private Genre genre;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        author = authorRepository.save(new Author(null, "Author_1"));
        genre = genreRepository.save(new Genre(null, "Genre_1"));
    }

    @Test
    @DisplayName("insert должен сохранять книгу с автором и жанрами")
    void insert_shouldSaveBook() {
        var book = bookService.insert("BookTitle_1", author.getId(), Set.of(genre.getId()));

        assertThat(book.getId()).isNotNull();
        assertThat(book.getTitle()).isEqualTo("BookTitle_1");
        assertThat(bookService.findById(book.getId())).isPresent();
    }

    @Test
    @DisplayName("findAll должен возвращать все книги")
    void findAll_shouldReturnAllBooks() {
        bookService.insert("Book_1", author.getId(), Set.of(genre.getId()));
        bookService.insert("Book_2", author.getId(), Set.of(genre.getId()));

        assertThat(bookService.findAll()).hasSize(2);
    }

    @Test
    @DisplayName("findById должен возвращать пустой Optional для несуществующей книги")
    void findById_shouldReturnEmptyForUnknownId() {
        assertThat(bookService.findById("000000000000000000000000")).isEmpty();
    }

    @Test
    @DisplayName("update должен изменять книгу")
    void update_shouldModifyBook() {
        var book = bookService.insert("Old", author.getId(), Set.of(genre.getId()));
        var updated = bookService.update(book.getId(), "New", author.getId(), Set.of(genre.getId()));

        assertThat(updated.getTitle()).isEqualTo("New");
        assertThat(bookService.findById(book.getId()))
                .get()
                .extracting("title")
                .isEqualTo("New");
    }

    @Test
    @DisplayName("deleteById должен удалять книгу и все её комментарии (нет каскадов в Mongo)")
    void deleteById_shouldDeleteBookAndComments() {
        var book = bookService.insert("Title", author.getId(), Set.of(genre.getId()));
        commentRepository.save(new Comment(null, "Nice", book));

        assertThat(commentRepository.findAllByBookId(book.getId())).hasSize(1);

        bookService.deleteById(book.getId());

        assertThat(bookService.findById(book.getId())).isEmpty();
        assertThat(commentRepository.findAllByBookId(book.getId())).isEmpty();
    }

    @Test
    @DisplayName("insert с несуществующим автором должен бросать EntityNotFoundException")
    void insert_withUnknownAuthor_shouldThrow() {
        assertThatThrownBy(() ->
                bookService.insert("Title", "000000000000000000000000", Set.of(genre.getId())))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("insert с несуществующим жанром должен бросать EntityNotFoundException")
    void insert_withUnknownGenre_shouldThrow() {
        assertThatThrownBy(() ->
                bookService.insert("Title", author.getId(), Set.of("000000000000000000000000")))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("insert с пустым набором жанров должен бросать IllegalArgumentException")
    void insert_withEmptyGenres_shouldThrow() {
        assertThatThrownBy(() ->
                bookService.insert("Title", author.getId(), Set.of()))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
