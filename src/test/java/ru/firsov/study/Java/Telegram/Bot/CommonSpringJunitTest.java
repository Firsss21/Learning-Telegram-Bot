package ru.firsov.study.Java.Telegram.Bot;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.firsov.study.Java.Telegram.Bot.test.Dog;
import ru.firsov.study.Java.Telegram.Bot.test.Pet;
import ru.firsov.study.Java.Telegram.Bot.test.PetService;
import ru.firsov.study.Java.Telegram.Bot.test.TestConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class})

// или
//@SpringJUnitConfig(classes = {TestConfig.class})
// для junit5


//@TestPropertySource("classpath:foobar.properties")
public class CommonSpringJunitTest {

    @Autowired
    PetService pet;

    @Test
    public void dog_sayHello() {
        Assert.assertEquals("I'm a DOG!", pet.getThisPetName());

    }

}
