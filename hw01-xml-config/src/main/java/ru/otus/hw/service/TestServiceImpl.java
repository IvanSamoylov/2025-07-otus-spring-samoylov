package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;
    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("Provide your name to start test:");
        String userName = ioService.readLine();
        ioService.printFormattedLine("Please answer the questions below%n");
        questionDao.findAll();
        // Получить вопросы из дао и вывести их с вариантами ответов
    }
}
