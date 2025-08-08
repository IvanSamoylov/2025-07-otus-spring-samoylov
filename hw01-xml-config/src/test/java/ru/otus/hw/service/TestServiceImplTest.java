package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class TestServiceImplTest {

    private IOService ioService;
    private QuestionDao questionDao;
    private TestServiceImpl testService;

    @BeforeEach
    void setUp() {
        ioService = mock(IOService.class);
        questionDao = mock(QuestionDao.class);
        testService = new TestServiceImpl(ioService, questionDao);
    }

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

        // when
        testService.executeTest();

        // then
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(ioService, times(3)).printLine(captor.capture());
        verify(ioService).printFormattedLine("Please answer the questions below%n");
        verify(ioService).printFormattedLine("Question: %s%n", question1.text());
        verify(ioService).printFormattedLine("\t%d: %s", 1, question1.answers().get(0).text());
        verify(ioService).printFormattedLine("\t%d: %s", 2, question1.answers().get(1).text());
        verify(ioService).printFormattedLine("Question: %s%n", question2.text());
        verify(ioService).printFormattedLine("\t%d: %s", 1, question2.answers().get(0).text());
        verify(ioService).printFormattedLine("\t%d: %s", 2, question2.answers().get(1).text());

        assertThat(captor.getAllValues()).containsExactly("", "", "");
    }

    @Test
    void shouldPrintEmptyPrompt() {
        // given
        given(questionDao.findAll()).willReturn(List.of());

        // when
        testService.executeTest();

        // then
        verify(ioService, times(1)).printLine("");
        verify(ioService, times(1)).printFormattedLine(anyString());
    }
}