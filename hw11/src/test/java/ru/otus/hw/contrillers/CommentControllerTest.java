package ru.otus.hw.contrillers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.otus.hw.controllers.CommentResrController;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.CommentService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@WebFluxTest(CommentResrController.class)
public class CommentControllerTest {
    @Autowired private WebTestClient webTestClient;
    @MockitoBean private CommentService commentService;

    @Test
    void delete_shouldCallServiceWithBookId() {
        when(commentService.deleteByIdAndBookId("c1", "b1")).thenReturn(Mono.empty());

        webTestClient.delete().uri("/api/books/b1/comments/c1")
                .exchange()
                .expectStatus().isNoContent();

        verify(commentService).deleteByIdAndBookId("c1", "b1");
    }

    @Test
    void delete_wrongBook_shouldReturn404() {
        when(commentService.deleteByIdAndBookId("c1", "b2"))
                .thenReturn(Mono.error(new EntityNotFoundException("does not belong")));

        webTestClient.delete().uri("/api/books/b2/comments/c1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void create_withEmptyText_shouldReturn400() {
        webTestClient.post().uri("/api/books/b1/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("""{"text":""}""")
                .exchange()
                .expectStatus().isBadRequest();

        verify(commentService, never()).insert(anyString(), anyString());
    }
}
