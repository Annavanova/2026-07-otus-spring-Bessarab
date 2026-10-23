package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

@Repository
public class BookRepositoryJpa implements BookRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Book> findById(long id) {
        return em.createQuery("""
                        select distinct b from Book b
                        join fetch b.author
                        left join fetch b.genres
                        where b.id = :id
                        """, Book.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Book> findAll() {
        return em.createQuery("""
                    select distinct b from Book b
                    join fetch b.author
                    left join fetch b.genres g
                    order by b.id, g.id
                    """, Book.class)
                .getResultList();
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            em.persist(book);
            return book;
        }
        return em.merge(book);
    }

    @Override
    public void deleteById(long id) {
        var book = em.find(Book.class, id);
        if (book != null) {
            em.remove(book);
        }
    }
}
