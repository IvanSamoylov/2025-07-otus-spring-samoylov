package ru.otus.hw.shell;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.service.TestRunnerService;

@ShellComponent
@RequiredArgsConstructor
public class TestRunnerCommand {

    private final TestRunnerService testRunnerService;

    @ShellMethod(value = "Run test", key = {"start"})
    public void runTest() {
        testRunnerService.run();
    }
}
