package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;

import java.util.List;
import java.util.Scanner;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService{
    private final IOService ioService;
    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("Welcome to the testing system!");

        // Запрашиваем данные студента
        ioService.printLine("Enter your first name:");
        String firstName = ioService.readString().trim();
        ioService.printLine("Enter your last name:");
        String lastName = ioService.readString().trim();
        Student student = new Student(firstName, lastName);

        ioService.printFormattedLine("Hello, %s!%n", student);

        // Получаем вопросы
        List<Question> questions = questionDao.findAll();

        if (questions.isEmpty()) {
            ioService.printLine("No questions available.");
            return;
        }

        ioService.printLine("Please answer the questions below:");

        // Тестирование
        int correctAnswers = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            ioService.printFormattedLine("%nQuestion %d:", i + 1);
            ioService.printLine(question.text());

            List<Answer> answers = question.answers();
            for (int j = 0; j < answers.size(); j++) {
                ioService.printFormattedLine("  %d. %s", j + 1, answers.get(j).text());
            }
            ioService.printLine("Enter the number of your answer:");

            String userAnswer = ioService.readString().trim();
            boolean isCorrect = false;
            try {
                int answerIndex = Integer.parseInt(userAnswer) - 1;
                if (answerIndex >= 0 && answerIndex < answers.size()) {
                    isCorrect = answers.get(answerIndex).isCorrect();
                }
            } catch (NumberFormatException e) {
                // Неверный ввод
            }

            if (isCorrect) {
                correctAnswers++;
                ioService.printLine("Correct!");
            } else {
                ioService.printLine("Incorrect.");
            }
        }

        // Вывод результатов (прямо здесь, без отдельного сервиса)
        double percentage = (double) correctAnswers / questions.size() * 100;
        ioService.printFormattedLine("%n--- Test Results ---");
        ioService.printFormattedLine("Student: %s", student);
        ioService.printFormattedLine("Correct answers: %d out of %d", correctAnswers, questions.size());
        ioService.printFormattedLine("Score: %.2f%%", percentage);

        if (percentage >= 80) {
            ioService.printLine("Congratulations! You passed the test!");
        } else {
            ioService.printLine("You need more practice. Try again!");
        }
    }
}
