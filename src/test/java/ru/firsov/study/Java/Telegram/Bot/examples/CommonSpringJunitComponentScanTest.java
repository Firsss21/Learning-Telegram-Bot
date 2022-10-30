package ru.firsov.study.Java.Telegram.Bot.examples;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.firsov.study.Java.Telegram.Bot.test.Pet;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {CommonSpringJunitComponentScanTest.TestConfiguration.class})
public class CommonSpringJunitComponentScanTest {

    @Configuration
    @ComponentScan("ru.firsov.study.Java.Telegram.Bot.test")
    static class TestConfiguration {

    }

    @Autowired
    Pet pet ;

    @Test
    public void pet_GetName() {
        Assert.assertEquals("I'm a DOG!", pet.getName());
    }
}
