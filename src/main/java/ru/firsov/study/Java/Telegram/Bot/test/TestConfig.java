package ru.firsov.study.Java.Telegram.Bot.test;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("test1")
@Configuration
public class TestConfig {

    @Bean
    Pet pet() {
        return new Dog();
    }

    @Bean
    PetService petService(Pet pet){
        return new PetService(pet);
    }

}
