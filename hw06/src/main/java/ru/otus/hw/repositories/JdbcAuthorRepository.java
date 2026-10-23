package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.otus.hw.models.Author;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
/*

@Repository
@RequiredArgsConstructor
public class JdbcAuthorRepository implements AuthorRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<Author> findAll() {
        //return new ArrayList<>();
        return jdbc.query("SELECT id, full_name FROM authors ORDER BY id", new AuthorRowMapper());
    }

    @Override
    public Optional<Author> findById(long id) {
        //return Optional.empty();
        var params = new MapSqlParameterSource("id", id);
        return jdbc.query(
                        "SELECT id, full_name FROM authors WHERE id = :id",
                        params,
                        new AuthorRowMapper())
                .stream()
                .findFirst();
    }

    private static class AuthorRowMapper implements RowMapper<Author> {

        @Override
        public Author mapRow(ResultSet rs, int i) throws SQLException {
            ///return null;
            return new Author(
                    rs.getLong("id"),
                    rs.getString("full_name"));
        }
    }
}
*/
