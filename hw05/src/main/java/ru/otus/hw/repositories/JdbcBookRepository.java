package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;
    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Book> findById(long id) {
        var params = new MapSqlParameterSource("id", id);
        var sql = """
                SELECT b.id        AS book_id,
                       b.title     AS book_title,
                       a.id        AS author_id,
                       a.full_name AS author_full_name,
                       g.id        AS genre_id,
                       g.name      AS genre_name
                FROM books b
                JOIN authors a ON a.id = b.author_id
                LEFT JOIN books_genres bg ON bg.book_id = b.id
                LEFT JOIN genres g ON g.id = bg.genre_id
                WHERE b.id = :id
                ORDER BY g.id
                """;
        return Optional.ofNullable(jdbc.query(sql, params, new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Override
    public void deleteById(long id) {
        var params = new MapSqlParameterSource("id", id);
        // Связи удалятся каскадно через ON DELETE CASCADE
        jdbc.update("DELETE FROM books WHERE id = :id", params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        ///return new ArrayList<>();
        var sql = """
                SELECT b.id        AS book_id,
                       b.title     AS book_title,
                       a.id        AS author_id,
                       a.full_name AS author_full_name
                FROM books b
                JOIN authors a ON a.id = b.author_id
                ORDER BY b.id
                """;
        return jdbc.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
       // return new ArrayList<>();
        return jdbc.query(
                "SELECT book_id, genre_id FROM books_genres",
                (rs, i) -> new BookGenreRelation(rs.getLong("book_id"), rs.getLong("genre_id")));

    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        // Добавить книгам (booksWithoutGenres) жанры (genres) в соответствии со связями (relations)
        //id жанра -> Genre, O(1) поиск
        Map<Long, Genre> genreById = genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity()));

        // id книги -> Book, O(1) поиск
        Map<Long, Book> bookById = booksWithoutGenres.stream()
                .collect(Collectors.toMap(Book::getId, Function.identity()));

        // Раскладываем жанры по книгам согласно связям
        for (var relation : relations) {
            var book = bookById.get(relation.bookId());
            var genre = genreById.get(relation.genreId());
            if (book != null && genre != null) {
                book.getGenres().add(genre);
            }
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        var params = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        jdbc.update(
                "INSERT INTO books (title, author_id) VALUES (:title, :author_id)",
                params,
                keyHolder,
                new String[]{"id"});

        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var params = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId());

        int updated = jdbc.update(
                "UPDATE books SET title = :title, author_id = :author_id WHERE id = :id",
                params);

        // если ни одна строка не обновлена — книги нет
        if (updated == 0) {
            throw new EntityNotFoundException("Book with id %d not found".formatted(book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);

        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        // Использовать метод batchUpdate
        if (book.getGenres() == null || book.getGenres().isEmpty()) {
            return;
        }
        // Один batchUpdate вместо N одиночных INSERT
        var batchParams = book.getGenres().stream()
                .map(genre -> new MapSqlParameterSource()
                        .addValue("book_id", book.getId())
                        .addValue("genre_id", genre.getId()))
                .toArray(MapSqlParameterSource[]::new);

        jdbc.batchUpdate(
                "INSERT INTO books_genres (book_id, genre_id) VALUES (:book_id, :genre_id)",
                batchParams);
    }

    private void removeGenresRelationsFor(Book book) {
        var params = new MapSqlParameterSource("book_id", book.getId());
        jdbc.update("DELETE FROM books_genres WHERE book_id = :book_id", params);
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var author = new Author(
                    rs.getLong("author_id"),
                    rs.getString("author_full_name"));
            return new Book(
                    rs.getLong("book_id"),
                    rs.getString("book_title"),
                    author,
                    new ArrayList<>());  // жанры добавим в mergeBooksInfo
        }
    }

    // Использовать для findById
    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            //return null;
            Book book = null;
            while (rs.next()) {
                if (book == null) {
                    var author = new Author(
                            rs.getLong("author_id"),
                            rs.getString("author_full_name"));
                    book = new Book(
                            rs.getLong("book_id"),
                            rs.getString("book_title"),
                            author,
                            new ArrayList<>());
                }
                var genreId = rs.getLong("genre_id");
                if (!rs.wasNull()) {
                    book.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
                }
            }
            return book;
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }
}
