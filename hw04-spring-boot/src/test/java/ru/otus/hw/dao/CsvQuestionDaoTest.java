package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;
import ru.otus.hw.service.TestServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = CsvQuestionDao.class)
class CsvQuestionDaoTest {

    @MockitoBean
    private TestFileNameProvider fileNameProvider;
    @Autowired
    private CsvQuestionDao dao;

    @BeforeEach
    void setUp() {
        given(fileNameProvider.getTestFileName()).willReturn("questions.csv");
    }

    @Test
    void shouldReadQuestionsFromCsv() {
        List<Question> questions = dao.findAll();

        assertNotNull(questions);
        assertFalse(questions.isEmpty());
        Question question = questions.get(0);
        assertEquals("Does coffee contain caffeine?", questions.get(0).text());
        List<Answer> answers = question.answers();
        assertEquals(answers.size(), 3);
        assertFalse(answers.get(0).isCorrect());
        assertTrue(answers.get(1).isCorrect());
        assertFalse(answers.get(2).isCorrect());
    }

    @Test
    void shouldThrowExceptionIfFileNotFound() {
        when(fileNameProvider.getTestFileName()).thenReturn("nonexistent.csv");

        QuestionReadException ex = assertThrows(
                QuestionReadException.class,
                () -> dao.findAll()
        );

        assertTrue(ex.getMessage().contains("file not found"));
    }
}