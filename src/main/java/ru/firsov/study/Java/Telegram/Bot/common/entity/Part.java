package ru.firsov.study.Java.Telegram.Bot.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "part")
    private List<Chapter> chapter;

    public List<Chapter> getChapter() {
        Hibernate.initialize(chapter);
        return chapter;
    }
}
