package ru.firsov.study.Java.Telegram.Bot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.service.QuestionService;

@EnableCaching
@SpringBootApplication
@EnableTransactionManagement
@Slf4j
public class JavaTelegramBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(JavaTelegramBotApplication.class, args);
	}

	@Bean
	@Transactional
	CommandLineRunner run() {
		return args -> {

		};
	}

}
