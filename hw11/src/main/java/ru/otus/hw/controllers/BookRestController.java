package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.services.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping
    public Flux<Book> findAll() {
        return bookService.findAll();
    }

    @GetMapping("/{id}")
    public Mono<Book> findById(@PathVariable String id) {
        return bookService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Book> create(@Valid @RequestBody BookFormDto dto) {
        return bookService.insert(dto.getTitle(), dto.getAuthorId(), dto.getGenreIds());
    }

    @PutMapping("/{id}")
    public Mono<Book> update(@PathVariable String id, @Valid @RequestBody BookFormDto dto) {
        return bookService.update(id, dto.getTitle(), dto.getAuthorId(), dto.getGenreIds());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return bookService.deleteById(id);
    }
}