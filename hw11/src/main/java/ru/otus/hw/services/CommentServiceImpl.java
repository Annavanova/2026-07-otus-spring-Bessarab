package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;

    @Override
    public Mono<Comment> findById(String id) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %s not found".formatted(id))))
                .flatMap(this::enrichComment);
    }

    @Override
    public Flux<Comment> findAllByBookId(String bookId) {
        return commentRepository.findAllByBookId(bookId).flatMap(this::enrichComment);
    }

    @Override
    public Mono<Comment> insert(String text, String bookId) {
        return bookRepository.findById(bookId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Book with id %s not found".formatted(bookId))))
                .flatMap(book -> commentRepository.save(new Comment(null, text, bookId))
                        .map(saved -> new Comment(saved.getId(), saved.getText(), book)));
    }

    @Override
    public Mono<Comment> update(String id, String text) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %s not found".formatted(id))))
                .flatMap(comment -> {
                    comment.setText(text);
                    return commentRepository.save(comment);
                })
                .flatMap(this::enrichComment);
    }

    @Override
    public Mono<Void> deleteById(String id) {
        return commentRepository.deleteById(id);
    }

    @Override
    public Mono<Void> deleteByIdAndBookId(String id, String bookId) {
        return commentRepository.findById(id)
                .switchIfEmpty(Mono.error(new EntityNotFoundException(
                        "Comment with id %s not found".formatted(id))))
                .flatMap(comment -> {
                    if (!bookId.equals(comment.getBookId())) {
                        return Mono.error(new EntityNotFoundException(
                                "Comment %s does not belong to book %s".formatted(id, bookId)));
                    }
                    return commentRepository.deleteById(id);
                });
    }

    private Mono<Comment> enrichComment(Comment comment) {
        if (comment == null) {
            return Mono.empty();
        }
        String bookId = comment.getBookId();
        if (bookId == null) {
            return Mono.just(new Comment(comment.getId(), comment.getText(), (Book) null));
        }
        return bookRepository.findById(bookId)
                .map(book -> new Comment(comment.getId(), comment.getText(), book))
                .defaultIfEmpty(new Comment(comment.getId(), comment.getText(), (Book) null));
    }
}