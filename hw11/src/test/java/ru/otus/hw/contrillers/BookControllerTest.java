package ru.otus.hw.contrillers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.BookRestController;
import ru.otus.hw.exceptions.EntityNotFoundException;
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

@WebFluxTest(BookRestController.class)
public class BookControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private BookService bookService;

    @Test
    void findAll_shouldReturnBooks() {
        var book = new Book("b1", "Book_1", new Author("a1", "A"), List.of());
        when(bookService.findAll()).thenReturn(Flux.just(book));

        webTestClient.get().uri("/api/books")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("b1")
                .jsonPath("$[0].title").isEqualTo("Book_1");
    }

    @Test
    void findById_shouldReturn404() {
        when(bookService.findById("unknown"))
                .thenReturn(Mono.error(new EntityNotFoundException("not found")));

        webTestClient.get().uri("/api/books/unknown")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void create_shouldReturn201() {
        var book = new Book("b1", "Book_1", new Author("a1", "A"), List.of());
        when(bookService.insert(anyString(), anyString(), anySet())).thenReturn(Mono.just(book));

        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"title":"Book_1","authorId":"a1","genreIds":["g1"]}
                        """)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().jsonPath("$.id").isEqualTo("b1");
    }

    @Test
    void create_withEmptyTitle_shouldReturn400() {
        webTestClient.post().uri("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""
                        {"title":"","authorId":"a1","genreIds":["g1"]}
                        """)
                .exchange()
                .expectStatus().isBadRequest();

        verify(bookService, never()).insert(anyString(), anyString(), anySet());
    }

    @Test
    void delete_shouldReturn204() {
        when(bookService.deleteById("b1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/b1")
                .exchange()
                .expectStatus().isNoContent();

        verify(bookService).deleteById("b1");
    }
}
