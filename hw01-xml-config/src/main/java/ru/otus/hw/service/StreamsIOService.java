package ru.otus.hw.service;

import java.io.PrintStream;
import java.util.Scanner;

public class StreamsIOService implements IOService {
    private final PrintStream printStream;
    private final Scanner scanner;

    public StreamsIOService(PrintStream printStream) {

        this.printStream = printStream;
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    @Override
    public String readLine() {
        String input = scanner.nextLine();
        while (input.trim().isEmpty()) {
            printLine("Empty name is not allowed. Input your name:");
            input = scanner.nextLine();
        }
        return input;
    }
}
