package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование TestServiceImpl")
class TestServiceImplTest {
    @Mock
    private IOService ioService;

    @Mock
    private QuestionDao questionDao;

    private TestService testService;

    @BeforeEach
    void setUp() {
        testService = new TestServiceImpl(ioService, questionDao);
    }

    @Test
    @DisplayName("Должен корректно проводить тест со всеми правильными ответами")
    void shouldExecuteTestWithAllCorrectAnswers() {
        List<Question> questions = Arrays.asList(
                new Question("What is 2+2?", Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                )),
                new Question("What is the capital of France?", Arrays.asList(
                        new Answer("Paris", true),
                        new Answer("London", false),
                        new Answer("Berlin", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString())
                .thenReturn("Ivan")      // first name
                .thenReturn("Ivanov")    // last name
                .thenReturn("2")         // ответ на первый вопрос (4)
                .thenReturn("1");        // ответ на второй вопрос (Paris)

        testService.executeTest();

        // Проверка
        verify(ioService, times(1)).printLine("Welcome to the testing system!");
        verify(ioService, times(1)).printLine("Enter your first name:");
        verify(ioService, times(1)).printLine("Enter your last name:");
        verify(ioService, times(1)).printFormattedLine("Hello, %s!%n", new Student("Ivan", "Ivanov"));
        verify(ioService, times(2)).printLine("Correct!");
        verify(ioService, never()).printLine("Incorrect.");
        verify(ioService, times(1)).printFormattedLine(contains("Score: 100.00%"));
        verify(ioService, times(1)).printLine("Congratulations! You passed the test!");
    }

    @Test
    @DisplayName("Должен корректно проводить тест со всеми неправильными ответами")
    void shouldExecuteTestWithAllIncorrectAnswers() {
        List<Question> questions = Arrays.asList(
                new Question("What is 2+2?", Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString())
                .thenReturn("Ivan")
                .thenReturn("Ivanov")
                .thenReturn("1"); // неправильный ответ (3)

        testService.executeTest();

        // Проверка
        verify(ioService, times(1)).printLine("Incorrect.");
        verify(ioService, never()).printLine("Correct.");
        verify(ioService, times(1)).printFormattedLine(contains("Score: 0.00%"));
        verify(ioService, times(1)).printLine("You need more practice. Try again!");
    }

    @Test
    @DisplayName("Должен корректно обрабатывать смешанные ответы")
    void shouldExecuteTestWithMixedAnswers() {
        List<Question> questions = Arrays.asList(
                new Question("What is 2+2?", Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                )),
                new Question("What is the capital of France?", Arrays.asList(
                        new Answer("Paris", true),
                        new Answer("London", false),
                        new Answer("Berlin", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString())
                .thenReturn("Ivan")
                .thenReturn("Ivanov")
                .thenReturn("2")  // правильный ответ на 1-й вопрос
                .thenReturn("2"); // неправильный ответ на 2-й вопрос (London)

        testService.executeTest();

        // Проверка
        verify(ioService, times(1)).printLine("Correct!");
        verify(ioService, times(1)).printLine("Incorrect.");
        verify(ioService, times(1)).printFormattedLine(contains("Score: 50.00%"));
        verify(ioService, times(1)).printLine("You need more practice. Try again!");
    }

    @Test
    @DisplayName("Должен корректно обрабатывать неверный ввод (не число)")
    void shouldHandleInvalidInput() {
        List<Question> questions = Arrays.asList(
                new Question("What is 2+2?", Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString())
                .thenReturn("Ivan")
                .thenReturn("Ivanov")
                .thenReturn("invalid")  // неверный ввод
                .thenReturn("10");      // выход за границы

        testService.executeTest();

        // Проверка - оба ответа должны быть неправильными
        verify(ioService, times(2)).printLine("Incorrect.");
        verify(ioService, never()).printLine("Correct.");
        verify(ioService, times(1)).printFormattedLine(contains("Score: 0.00%"));
    }

    @Test
    @DisplayName("Должен корректно обрабатывать случай с пустым списком вопросов")
    void shouldHandleEmptyQuestionsList() {
        when(questionDao.findAll()).thenReturn(Collections.emptyList());
        when(ioService.readString())
                .thenReturn("Ivan")
                .thenReturn("Ivanov");

        testService.executeTest();

        verify(ioService, times(1)).printLine("No questions available.");
        verify(ioService, never()).printLine("Please answer the questions below:");
        verify(ioService, never()).printFormattedLine(contains("Score:"));
    }

    @Test
    @DisplayName("Должен корректно выводить вопросы с вариантами ответов")
    void shouldPrintQuestionsWithOptions() {
        List<Question> questions = Arrays.asList(
                new Question("What is 2+2?", Arrays.asList(
                        new Answer("3", false),
                        new Answer("4", true),
                        new Answer("5", false)
                ))
        );

        when(questionDao.findAll()).thenReturn(questions);
        when(ioService.readString())
                .thenReturn("Ivan")
                .thenReturn("Ivanov")
                .thenReturn("2");

        testService.executeTest();

        verify(ioService, times(1)).printLine("What is 2+2?");
        verify(ioService, times(1)).printFormattedLine("  1. %s", "3");
        verify(ioService, times(1)).printFormattedLine("  2. %s", "4");
        verify(ioService, times(1)).printFormattedLine("  3. %s", "5");
        verify(ioService, times(1)).printLine("Enter the number of your answer:");
    }

}