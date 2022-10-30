package ru.firsov.study.Java.Telegram.Bot.test;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Profile("test1")
@Service
public class PetService {

    private Pet pet;

    public PetService(Pet pet) {
        this.pet = pet;
    }

    public String getThisPetName() {
        return pet.getName();
    }
}
