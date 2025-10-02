package ru.otus.hw.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Репозиторий для работы с жанрами должен")
@JdbcTest
@Import(JdbcGenreRepository.class)
class JdbcGenreRepositoryTest {
    private static final int EXISTING_GENRE_ID = 1;
    private static final String EXISTING_GENRE_NAME = "Genre_1";

    @Autowired
    private JdbcGenreRepository genreRepository;

    @DisplayName("возвращать ожидаемый жанр по его id")
    @Test
    void shouldReturnExpectedGenreById() {
        Genre expectedGenre = new Genre(EXISTING_GENRE_ID, EXISTING_GENRE_NAME);
        Genre actualGenre = genreRepository.findById(expectedGenre.getId()).get();
        assertThat(actualGenre).usingRecursiveComparison().isEqualTo(expectedGenre);
    }

    @DisplayName("возвращать ожидаемый список жанров")
    @Test
    void shouldReturnExpectedGenresList() {
        Genre expectedGenre1 = new Genre(1, "Genre_1");
        Genre expectedGenre2 = new Genre(2, "Genre_2");
        Genre expectedGenre3 = new Genre(3, "Genre_3");

        List<Genre> actualGenreList = genreRepository.findAll();
        assertThat(actualGenreList)
                .containsExactlyInAnyOrder(expectedGenre1, expectedGenre2, expectedGenre3);
    }
}