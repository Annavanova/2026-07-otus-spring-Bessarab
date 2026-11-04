package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final MongoOperations mongoOperations;

    @Override
    public boolean existsByAuthorId(String authorId) {
        var query = Query.query(Criteria.where("author.$id").is(new org.bson.types.ObjectId(authorId)));
        return mongoOperations.exists(query, Book.class);
    }
}
