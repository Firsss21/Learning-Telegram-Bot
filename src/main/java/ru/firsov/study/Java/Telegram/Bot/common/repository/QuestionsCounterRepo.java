package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.repository.CrudRepository;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;
import ru.firsov.study.Java.Telegram.Bot.common.entity.QuestionsCounter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import java.util.Optional;

public interface QuestionsCounterRepo extends CrudRepository<QuestionsCounter, Long> {
}
