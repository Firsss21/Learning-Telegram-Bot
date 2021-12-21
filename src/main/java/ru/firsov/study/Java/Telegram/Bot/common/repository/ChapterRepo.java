package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.repository.CrudRepository;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Part;

import java.util.List;

public interface ChapterRepo extends CrudRepository<Chapter, Long> {
    List<Chapter> findAllByPart(Part part);
    Chapter findByName(String name);
}
