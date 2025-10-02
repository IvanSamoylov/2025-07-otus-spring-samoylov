package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = TestServiceImpl.class)
class TestServiceImplTest {

    @MockitoBean
    private LocalizedIOService ioService;
    @MockitoBean
    private QuestionDao questionDao;
    @Autowired
    private TestServiceImpl testService;
    private Student student = new Student("name", "lastName");

    @Test
    void shouldPrintQuestionsAndAnswers() {
        // given
        Answer answer1 = new Answer("Answer 1", false);
        Answer answer2 = new Answer("Answer 2", true);
        Question question1 = new Question("What is Question1?", List.of(answer1, answer2));
        Answer answer3 = new Answer("Answer 3", false);
        Answer answer4 = new Answer("Answer 4", true);
        Question question2 = new Question("What is Question2?", List.of(answer3, answer4));

        given(questionDao.findAll()).willReturn(List.of(question1,question2));
        given(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .willReturn(1);

        // when
        testService.executeTestFor(student);

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ioService, times(4)).printLine(captor.capture());
        verify(ioService).printLineLocalized("TestService.answer.the.questions");
        verify(ioService).printFormattedLineLocalized("TestService.question", question1.text());
        verify(ioService).printFormattedLine("\t%d: %s", 1, question1.answers().get(0).text());
        verify(ioService).printFormattedLine("\t%d: %s", 2, question1.answers().get(1).text());
        verify(ioService).printFormattedLineLocalized("TestService.question", question2.text());
        verify(ioService).printFormattedLine("\t%d: %s", 1, question2.answers().get(0).text());
        verify(ioService).printFormattedLine("\t%d: %s", 2, question2.answers().get(1).text());

        assertThat(captor.getAllValues()).containsExactly("", "", "","");
    }

    @Test
    void shouldHandleExceedingNumberOfAnswers() {
        // given
        Answer answer1 = new Answer("Answer 1", false);
        Answer answer2 = new Answer("Answer 2", true);
        Question question1 = new Question("What is Question1?", List.of(answer1, answer2));

        given(questionDao.findAll()).willReturn(List.of(question1));
        given(ioService.readIntForRangeWithPromptLocalized(anyInt(), anyInt(), anyString(), anyString()))
                .willThrow(new IllegalArgumentException("Too many attempts"));

        // when
        TestResult result = testService.executeTestFor(student);

        // then
        assertNotNull(result);
        verify(ioService, times(3)).printLine(anyString());
    }

    @Test
    void shouldPrintEmptyPrompt() {
        // given
        given(questionDao.findAll()).willReturn(List.of());

        // when
        testService.executeTestFor(student);

        // then
        verify(ioService, times(2)).printLine("");
        verify(ioService, times(1)).printLineLocalized(anyString());
    }
}