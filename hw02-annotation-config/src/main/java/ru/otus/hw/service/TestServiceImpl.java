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

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (var question : questions) {
            var isAnswerValid = processQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean processQuestion(Question question) {
        ioService.printFormattedLine("Question: %s%n", question.text());
        List<Answer> answers = question.answers();
        for (int i = 0; i < answers.size(); i++) {
            ioService.printFormattedLine("\t%d: %s", i + 1, answers.get(i).text());
        }
        ioService.printLine("");
        try {
            int answerIndex = ioService.readIntForRangeWithPrompt(1, answers.size(), "Select correct number",
                    "Incorrect number. Please try again");
            var studentAnswer = answers.get(answerIndex);
            return studentAnswer.isCorrect();
        } catch (IllegalArgumentException e) {
            ioService.printLine("Exceeding number of attempts for the question.");
        }
        return false;
    }
}
