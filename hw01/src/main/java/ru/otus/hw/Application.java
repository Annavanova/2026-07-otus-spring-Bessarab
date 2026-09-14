package ru.otus.hw;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import ru.otus.hw.service.IOService;
import ru.otus.hw.service.TestRunnerService;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("spring-context.xml");
		var testRunnerService = context.getBean(TestRunnerService.class);
		testRunnerService.run();
	}
}
