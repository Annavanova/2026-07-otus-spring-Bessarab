package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

@RequiredArgsConstructor
public class BookRepositoryCustomImpl implements BookRepositoryCustom {

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Mono<Boolean> existsByAuthorId(String authorId) {
        if (authorId == null || !ObjectId.isValid(authorId)) {
            return Mono.just(false);
        }
        var query = Query.query(Criteria.where("authorId").is(authorId));
        return mongoTemplate.exists(query, Book.class);
    }
}