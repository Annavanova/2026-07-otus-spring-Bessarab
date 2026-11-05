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

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataMongoTest
@Import({CommentServiceImpl.class, BookServiceImpl.class,
        AuthorServiceImpl.class, GenreServiceImpl.class})
@DisplayName("CommentService (MongoDB)")
public class CommentServiceTest {

    @Autowired private CommentService commentService;
    @Autowired private BookService bookService;
    @Autowired private AuthorRepository authorRepository;
    @Autowired private GenreRepository genreRepository;
    @Autowired private CommentRepository commentRepository;

    private String bookId;

    @BeforeEach
    void setUp() {
        commentRepository.deleteAll();
        authorRepository.deleteAll();
        genreRepository.deleteAll();

        var author = authorRepository.save(new Author(null, "A"));
        var genre = genreRepository.save(new Genre(null, "G"));
        bookId = bookService.insert("Book", author.getId(), Set.of(genre.getId())).getId();
    }

    @Test
    @DisplayName("insert должен сохранять комментарий к книге")
    void insert_shouldSaveComment() {
        var comment = commentService.insert("Nice", bookId);

        assertThat(comment.getId()).isNotNull();
        assertThat(commentService.findById(comment.getId())).isPresent();
    }

    @Test
    @DisplayName("insert с несуществующей книгой должен бросать EntityNotFoundException")
    void insert_unknownBook_shouldThrow() {
        assertThatThrownBy(() -> commentService.insert("Text", "000000000000000000000000"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("findAllByBookId должен возвращать комментарии только этой книги")
    void findAllByBookId_shouldReturnOnlyThisBookComments() {
        commentService.insert("C1", bookId);
        commentService.insert("C2", bookId);

        assertThat(commentService.findAllByBookId(bookId))
                .hasSize(2)
                .extracting(Comment::getText)
                .containsExactlyInAnyOrder("C1", "C2");
    }

    @Test
    @DisplayName("update должен изменять текст комментария")
    void update_shouldModifyText() {
        var comment = commentService.insert("Old", bookId);
        var updated = commentService.update(comment.getId(), "New");

        assertThat(updated.getText()).isEqualTo("New");
    }

    @Test
    @DisplayName("update с несуществующим id должен бросать EntityNotFoundException")
    void update_unknown_shouldThrow() {
        assertThatThrownBy(() -> commentService.update("000000000000000000000000", "New"))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("deleteById должен удалять комментарий")
    void deleteById_shouldDeleteComment() {
        var comment = commentService.insert("Text", bookId);
        commentService.deleteById(comment.getId());
        assertThat(commentService.findById(comment.getId())).isEmpty();
    }
}
