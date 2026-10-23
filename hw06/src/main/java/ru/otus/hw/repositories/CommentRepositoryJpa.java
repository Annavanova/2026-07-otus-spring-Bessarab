package ru.otus.hw.repositories;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Optional;

@Repository
public class CommentRepositoryJpa implements CommentRepository{
    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Comment> findById(long id) {
        // book тянем через JOIN FETCH, чтобы снаружи можно было обратиться к comment.getBook()
        return em.createQuery("""
                        select c from Comment c
                        join fetch c.book
                        where c.id = :id
                        """, Comment.class)
                .setParameter("id", id)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Override
    public List<Comment> findAllByBookId(long bookId) {
        return em.createQuery("""
                        select c from Comment c
                        where c.book.id = :bookId
                        order by c.id
                        """, Comment.class)
                .setParameter("bookId", bookId)
                .getResultList();
    }

    @Override
    public Comment save(Comment comment) {
        if (comment.getId() == 0) {
            em.persist(comment);
            return comment;
        }
        return em.merge(comment);
    }

    @Override
    public void deleteById(long id) {
        var comment = em.find(Comment.class, id);
        if (comment != null) {
            em.remove(comment);
        }
    }
}
