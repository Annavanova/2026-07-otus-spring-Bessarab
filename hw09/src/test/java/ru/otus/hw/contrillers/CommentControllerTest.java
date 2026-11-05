package ru.otus.hw.contrillers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private CommentService commentService;

    @Test
    void listComments() throws Exception {
        when(commentService.findAllByBookId("b1")).thenReturn(List.of());

        mockMvc.perform(get("/books/b1/comments"))
                .andExpect(status().isOk())
                .andExpect(view().name("comments/list"))
                .andExpect(model().attributeExists("comments", "bookId"));
    }

    @Test
    void createComment() throws Exception {
        when(commentService.insert(anyString(), anyString()))
                .thenReturn(new Comment("c1", "Text", null));

        mockMvc.perform(post("/books/b1/comments").param("text", "Nice"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/b1/comments"));

        verify(commentService).insert("Nice", "b1");
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(post("/comments/c1/delete").param("bookId", "b1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/b1/comments"));

        verify(commentService).deleteById("c1");
    }
}
