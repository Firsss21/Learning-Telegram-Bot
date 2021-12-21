package ru.firsov.study.Java.Telegram.Bot.common.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(columnDefinition="TEXT")
    private String question;

    @Column(columnDefinition="TEXT")
    private String answer;

    @ManyToOne
    @JoinColumn(name = "chapter_id")
    private Chapter chapter;

}
