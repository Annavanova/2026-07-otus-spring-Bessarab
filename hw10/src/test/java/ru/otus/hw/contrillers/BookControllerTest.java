package ru.otus.hw.contrillers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookRestController;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookRestController.class)
@DisplayName("BookRestController (REST)")
public class BookControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private BookService bookService;

    private Book sampleBook() {
        var author = new Author("a1", "Author_1");
        var genre = new Genre("g1", "Genre_1");
        return new Book("b1", "Book_1", author, List.of(genre));
    }

    @Test
    @DisplayName("GET /api/books — список книг")
    void findAll() throws Exception {
        when(bookService.findAll()).thenReturn(List.of(sampleBook()));

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("b1"))
                .andExpect(jsonPath("$[0].title").value("Book_1"));
    }

    @Test
    @DisplayName("GET /api/books/{id} — книга по id")
    void findById() throws Exception {
        when(bookService.findById("b1")).thenReturn(Optional.of(sampleBook()));

        mockMvc.perform(get("/api/books/b1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Book_1"));
    }

    @Test
    @DisplayName("GET /api/books/{id} — 404 для несуществующей")
    void findById_notFound() throws Exception {
        when(bookService.findById("unknown")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/books/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/books — создание")
    void create() throws Exception {
        when(bookService.insert(anyString(), anyString(), anySet()))
                .thenReturn(sampleBook());

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Book_1","authorId":"a1","genreIds":["g1"]}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("b1"));
    }

    @Test
    @DisplayName("PUT /api/books/{id} — обновление")
    void update() throws Exception {
        when(bookService.update(anyString(), anyString(), anyString(), anySet()))
                .thenReturn(sampleBook());

        mockMvc.perform(put("/api/books/b1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Updated","authorId":"a1","genreIds":["g1"]}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("b1"));
    }

    @Test
    @DisplayName("DELETE /api/books/{id} — удаление")
    void deleteBook() throws Exception {
        mockMvc.perform(delete("/api/books/b1"))
                .andExpect(status().isNoContent());

        verify(bookService).deleteById("b1");
    }
}
