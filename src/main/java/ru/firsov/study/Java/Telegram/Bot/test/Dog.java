package ru.firsov.study.Java.Telegram.Bot.test;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

//@Primary
@Component
public class Dog implements Pet {

    @Override
    public String getName() {
        return "I'm a DOG!";
    }

}
