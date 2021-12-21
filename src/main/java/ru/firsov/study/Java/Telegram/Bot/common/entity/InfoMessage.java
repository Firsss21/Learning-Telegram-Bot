package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Data
@NoArgsConstructor
public class InfoMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private MessageType messageType;

    private ZonedDateTime date;

    public InfoMessage(User user, MessageType messageType) {
        this.user = user;
        this.messageType = messageType;
        this.date = ZonedDateTime.now();
    }
}


