package ru.otus.hw.repositories;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcBookRepository implements BookRepository {

    private final NamedParameterJdbcOperations namedParameterJdbcOperations;

    public JdbcBookRepository(NamedParameterJdbcOperations namedParameterJdbcOperations) {
        this.namedParameterJdbcOperations = namedParameterJdbcOperations;
    }

    @Override
    public Optional<Book> findById(long id) {
        var book = namedParameterJdbcOperations.queryForObject("""
                SELECT b.id AS book_id, b.title AS book_title, a.id AS author_id, a.full_name AS author_name,
                g.id AS genre_id, g.name AS genre_name
                FROM books b
                    JOIN authors a ON b.author_id = a.id
                    JOIN genres g ON b.genre_id = g.id
                WHERE b.id = :id
                """, new MapSqlParameterSource("id", id), new BookRowMapper());
        return Optional.ofNullable(book);
    }

    @Override
    public List<Book> findAll() {
        var books = namedParameterJdbcOperations.query("""
                SELECT b.id AS book_id, b.title AS book_title, a.id AS author_id, a.full_name AS author_name,
                g.id AS genre_id, g.name AS genre_name
                FROM books b
                    JOIN authors a ON b.author_id = a.id
                    JOIN genres g ON b.genre_id = g.id""", new BookRowMapper());
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
        namedParameterJdbcOperations.update("delete from books where id=:id", new MapSqlParameterSource("id", id));
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();

        var parameters = new MapSqlParameterSource()
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId())
                .addValue("genre_id", book.getGenre().getId());

        namedParameterJdbcOperations.update(
                "INSERT INTO books (title, author_id, genre_id) VALUES (:title, :author_id, :genre_id)",
                parameters,
                keyHolder,
                new String[]{"id"}
        );

        book.setId(keyHolder.getKeyAs(Long.class));
        return book;
    }

    private Book update(Book book) {
        var parameters = new MapSqlParameterSource()
                .addValue("id", book.getId())
                .addValue("title", book.getTitle())
                .addValue("author_id", book.getAuthor().getId())
                .addValue("genre_id", book.getGenre().getId());

        int updatedRows = namedParameterJdbcOperations.update(
                "UPDATE books SET title = :title, author_id = :author_id, genre_id = :genre_id WHERE id = :id",
                parameters
        );

        if (updatedRows == 0) {
            throw new EntityNotFoundException("Book with id " + book.getId() + " not found");
        }
        return book;
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            Author author = new Author(
                    rs.getLong("author_id"),
                    rs.getString("author_name")
            );

            Genre genre = new Genre(
                    rs.getLong("genre_id"),
                    rs.getString("genre_name")
            );

            return new Book(
                    rs.getLong("book_id"),
                    rs.getString("book_title"),
                    author,
                    genre
            );
        }
    }
}
