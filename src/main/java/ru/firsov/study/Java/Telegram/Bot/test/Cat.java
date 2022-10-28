package ru.firsov.study.Java.Telegram.Bot.test;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("cat")
public class Cat implements Pet {

    @Override
    public String getName() {
        return "I'm a CAT";
    }
}
