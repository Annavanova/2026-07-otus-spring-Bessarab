package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;

import java.util.List;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Override
    public Author findById(String id) {
        return authorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(id)));
    }

    @Transactional
    public void deleteById(String id) {
        // Запрет удаления, если у автора есть книги — иначе книги станут неконсистентными
        if (bookRepository.existsByAuthorId(id)) {
            throw new EntityNotFoundException(
                    "Cannot delete author %s: books exist".formatted(id));
        }
        authorRepository.deleteById(id);
    }
}
