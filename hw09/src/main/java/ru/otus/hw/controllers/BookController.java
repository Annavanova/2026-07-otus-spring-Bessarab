package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.BookFormDto;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.GenreService;

import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final AuthorService authorService;
    private final GenreService genreService;

    @GetMapping({"/", "/books"})
    public String listBooks(Model model) {
        model.addAttribute("books", bookService.findAll());
        return "books/list";
    }

    @GetMapping("/books/{id}")
    public String viewBook(@PathVariable String id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        model.addAttribute("book", book);
        return "books/view";
    }

    @GetMapping("/books/new")
    public String createForm(Model model) {
        model.addAttribute("book", new BookFormDto());
        addFormLists(model);
        return "books/form";
    }

    @PostMapping("/books")
    public String create(@ModelAttribute("book") BookFormDto dto, Model model) {
        if (!validate(dto, model)) {
            return "books/form";
        }
        bookService.insert(dto.getTitle(), dto.getAuthorId(), dto.getGenreIds());
        return "redirect:/books";
    }

    @GetMapping("/books/{id}/edit")
    public String editForm(@PathVariable String id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        var dto = new BookFormDto();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthorId(book.getAuthor().getId());
        dto.setGenreIds(book.getGenres().stream().map(g -> g.getId()).collect(Collectors.toSet()));
        model.addAttribute("book", dto);
        addFormLists(model);
        return "books/form";
    }

    @PostMapping("/books/{id}")
    public String update(@PathVariable String id, @ModelAttribute("book") BookFormDto dto, Model model) {
        if (!validate(dto, model)) {
            return "books/form";   // остаёмся на форме с ошибкой
        }
        bookService.update(id, dto.getTitle(), dto.getAuthorId(), dto.getGenreIds());
        return "redirect:/books";
    }

    @GetMapping("/books/{id}/delete")
    public String confirmDelete(@PathVariable String id, Model model) {
        var book = bookService.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Book with id %s not found".formatted(id)));
        model.addAttribute("book", book);
        return "books/delete";
    }

    @PostMapping("/books/{id}/delete")
    public String delete(@PathVariable String id) {
        bookService.deleteById(id);
        return "redirect:/books";
    }

    /**
     * Единая валидация формы книги.
     * Возвращает true, если форма валидна; иначе кладёт ошибку в модель и возвращает false.
     */
    private boolean validate(BookFormDto dto, Model model) {
        if (dto.getTitle() == null || dto.getTitle().isBlank()) {
            model.addAttribute("error", "Title must not be empty");
            addFormLists(model);
            return false;
        }
        if (dto.getAuthorId() == null || dto.getAuthorId().isBlank()) {
            model.addAttribute("error", "Please select an author");
            addFormLists(model);
            return false;
        }
        if (dto.getGenreIds() == null || dto.getGenreIds().isEmpty()) {
            model.addAttribute("error", "Please select at least one genre");
            addFormLists(model);
            return false;
        }
        return true;
    }

    private void addFormLists(Model model) {
        model.addAttribute("authors", authorService.findAll());
        model.addAttribute("genres", genreService.findAll());
    }
}
