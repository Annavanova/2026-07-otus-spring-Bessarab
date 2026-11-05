package ru.otus.hw.contrillers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
@DisplayName("BookController (WebMvcTest)")
public class BookControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private BookService bookService;
    @MockitoBean private AuthorService authorService;
    @MockitoBean private GenreService genreService;

    private Book sampleBook() {
        var author = new Author("a1", "Author_1");
        var genre = new Genre("g1", "Genre_1");
        return new Book("b1", "Book_1", author, List.of(genre));
    }

    @Test
    @DisplayName("GET /books — список книг")
    void listBooks() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(sampleBook()));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/list"))
                .andExpect(model().attributeExists("books"));

        verify(bookService).findAll();
    }

    @Test
    @DisplayName("GET /books/{id} — просмотр книги")
    void viewBook() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(sampleBook()));

        mockMvc.perform(get("/books/b1"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/view"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("GET /books/new — форма создания")
    void createForm() throws Exception {
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book", "authors", "genres"));
    }

    @Test
    @DisplayName("POST /books — создание книги")
    void createBook() throws Exception {
        when(bookService.insert(anyString(), anyString(), anySet()))
                .thenReturn(sampleBook());

        mockMvc.perform(post("/books")
                        .param("title", "Book_1")
                        .param("authorId", "a1")
                        .param("genreIds", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).insert(eq("Book_1"), eq("a1"), anySet());
    }

    @Test
    @DisplayName("GET /books/{id}/edit — форма редактирования")
    void editForm() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(sampleBook()));
        when(authorService.findAll()).thenReturn(List.of());
        when(genreService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/books/b1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/form"))
                .andExpect(model().attributeExists("book", "authors", "genres"));
    }

    @Test
    @DisplayName("POST /books/{id} — обновление книги")
    void updateBook() throws Exception {
        when(bookService.update(anyString(), anyString(), anyString(), anySet()))
                .thenReturn(sampleBook());

        mockMvc.perform(post("/books/b1")
                        .param("title", "Updated")
                        .param("authorId", "a1")
                        .param("genreIds", "g1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).update(eq("b1"), eq("Updated"), eq("a1"), anySet());
    }

    @Test
    @DisplayName("GET /books/{id}/delete — страница подтверждения")
    void confirmDelete() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(sampleBook()));

        mockMvc.perform(get("/books/b1/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("books/delete"))
                .andExpect(model().attributeExists("book"));
    }

    @Test
    @DisplayName("POST /books/{id}/delete — удаление книги")
    void deleteBook() throws Exception {
        mockMvc.perform(post("/books/b1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books"));

        verify(bookService).deleteById("b1");
    }

    @Test
    @DisplayName("GET /books/{id}/delete не должен удалять (только страница подтверждения)")
    void getDelete_shouldNotDelete() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(sampleBook()));

        mockMvc.perform(get("/books/b1/delete"))
                .andExpect(status().isOk());

        verify(bookService, never()).deleteById(anyString());
    }
}
