package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = processQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean processQuestion(Question question) {
        ioService.printFormattedLineLocalized("TestService.question", question.text());
        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("\t%d: %s", i + 1, answers.get(i).text());
        }
        ioService.printLine("");
        try {
            int answerIndex = ioService.readIntForRangeWithPromptLocalized(1, answers.size(),
                    "TestService.question.prompt","TestService.question.error");
            var studentAnswer = answers.get(answerIndex - 1);
            return studentAnswer.isCorrect();
        } catch (IllegalArgumentException e) {
            ioService.printLineLocalized("TestService.question.exceeding.attempts");
        }
        return false;
    }
}
