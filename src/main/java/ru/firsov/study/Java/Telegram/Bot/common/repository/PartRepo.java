package ru.firsov.study.Java.Telegram.Bot.common.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Part;

import java.util.List;

@Transactional
public interface PartRepo extends CrudRepository<Part, Long> {
    List<Part> findAll();
    Part findByName(String name);
}
