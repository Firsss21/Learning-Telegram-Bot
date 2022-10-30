package ru.firsov.study.Java.Telegram.Bot.examples;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.firsov.study.Java.Telegram.Bot.test.Cat;
import ru.firsov.study.Java.Telegram.Bot.test.Pet;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CommonSpringJunitInnerTest.PetConfig.class})
public class CommonSpringJunitInnerTest {

    @Configuration
    static class PetConfig {
        @Bean
        Pet pet() {
            return new Cat();
        }
    }

    @Autowired
    Pet pet;

    @Test
    public void cat_getName() {
        Assert.assertEquals("I'm a CAT", pet.getName());
    }
}
