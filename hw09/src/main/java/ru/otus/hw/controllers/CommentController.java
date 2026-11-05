package ru.otus.hw.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.dto.CommentFormDto;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @GetMapping("/books/{bookId}/comments")
    public String list(@PathVariable String bookId, Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("comments", commentService.findAllByBookId(bookId));
        model.addAttribute("comment", new CommentFormDto());
        return "comments/list";
    }

    @PostMapping("/books/{bookId}/comments")
    public String create(@PathVariable String bookId, @ModelAttribute CommentFormDto dto) {
        commentService.insert(dto.getText(), bookId);
        return "redirect:/books/" + bookId + "/comments";
    }

    @PostMapping("/comments/{id}/delete")
    public String delete(@PathVariable String id, @RequestParam String bookId) {
        commentService.deleteById(id);
        return "redirect:/books/" + bookId + "/comments";
    }
}
