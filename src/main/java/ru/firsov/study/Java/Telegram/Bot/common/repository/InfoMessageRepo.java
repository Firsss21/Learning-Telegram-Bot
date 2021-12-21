package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.firsov.study.Java.Telegram.Bot.common.entity.InfoMessage;

public interface InfoMessageRepo extends JpaRepository<InfoMessage, Long> {
}
