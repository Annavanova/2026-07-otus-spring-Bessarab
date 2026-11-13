package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentFormDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

@RestController
@RequestMapping("/api/books/{bookId}/comments")
@RequiredArgsConstructor
public class CommentResrController {

    private final CommentService commentService;

    @GetMapping
    public Flux<Comment> findAll(@PathVariable String bookId) {
        return commentService.findAllByBookId(bookId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Comment> create(@PathVariable String bookId, @Valid @RequestBody CommentFormDto dto) {
        return commentService.insert(dto.getText(), bookId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String bookId, @PathVariable String commentId) {
        return commentService.deleteByIdAndBookId(commentId, bookId);
    }
}