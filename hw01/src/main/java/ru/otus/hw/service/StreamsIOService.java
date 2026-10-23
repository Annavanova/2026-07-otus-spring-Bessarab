package ru.otus.hw.service;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Scanner;

public class StreamsIOService implements IOService{
    private final PrintStream printStream;
    private final Scanner scanner;  // ДОБАВЛЯЕМ

    public StreamsIOService(PrintStream printStream, InputStream inputStream) {
        this.printStream = printStream;
        this.scanner = new Scanner(inputStream);  // ДОБАВЛЯЕМ
    }

    @Override
    public void printLine(String s) {
        printStream.println(s);
    }

    @Override
    public void printFormattedLine(String s, Object... args) {
        printStream.printf(s + "%n", args);
    }

    // ДОБАВЛЯЕМ
    @Override
    public String readString() {
        return scanner.nextLine();
    }
}
