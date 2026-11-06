package ru.otus.hw.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping({"/", "/books"})
    public String booksPage() {
        return "books/list";
    }

    @GetMapping("/books/new")
    public String createForm() {
        return "books/form";
    }

    @GetMapping("/books/{id}/edit")
    public String editForm() {
        return "books/form";
    }

    @GetMapping("/authors")
    public String authorsPage() {
        return "authors/list";
    }

    @GetMapping("/genres")
    public String genresPage() {
        return "genres/list";
    }

    @GetMapping("/books/{bookId}/comments")
    public String commentsPage() {
        return "comments/list";
    }
}
