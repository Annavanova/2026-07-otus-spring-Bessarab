package ru.otus.hw.contrillers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.CommentResrController;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentResrController.class)
public class CommentControllerTest {
    @Autowired private MockMvc mockMvc;
    @MockitoBean private CommentService commentService;

    @Test
    @DisplayName("DELETE /api/books/{bookId}/comments/{id} — 204")
    void delete_ok() throws Exception {
        mockMvc.perform(delete("/api/books/b1/comments/c1"))
                .andExpect(status().isNoContent());

        verify(commentService).deleteByIdAndBookId("c1", "b1");
    }

    @Test
    @DisplayName("DELETE /api/books/{bookId}/comments/{id} — 404, если чужой комментарий")
    void delete_wrongBook_shouldReturn404() throws Exception {
        doThrow(new EntityNotFoundException("Comment c1 does not belong to book b2"))
                .when(commentService).deleteByIdAndBookId("c1", "b2");

        mockMvc.perform(delete("/api/books/b2/comments/c1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST с пустым text — 400")
    void create_withEmptyText_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/books/b1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"text":""}
                                """))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).insert(anyString(), anyString());
    }
}
