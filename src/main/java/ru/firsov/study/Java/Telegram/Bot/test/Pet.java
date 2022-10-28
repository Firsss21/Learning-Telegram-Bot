package ru.firsov.study.Java.Telegram.Bot.test;

import org.springframework.stereotype.Component;

public interface Pet {

    String getName();

    default void sayName() {
        System.out.println(getName());
    }
}
