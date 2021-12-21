package ru.firsov.study.Java.Telegram.Bot.common.service;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.firsov.study.Java.Telegram.Bot.common.BotState;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Chapter;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Part;
import ru.firsov.study.Java.Telegram.Bot.common.entity.Question;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;
import ru.firsov.study.Java.Telegram.Bot.common.repository.ChapterRepo;
import ru.firsov.study.Java.Telegram.Bot.common.repository.PartRepo;
import ru.firsov.study.Java.Telegram.Bot.common.repository.QuestionRepo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional
public class QuestionService {

    private final QuestionRepo questionRepo;
    private final PartRepo partRepo;
    private final ChapterRepo chapterRepo;

    public void saveQuestion(Question question) {
        questionRepo.save(question);
    }

    public void savePart(Part part) {
        partRepo.save(part);
    }

    public void saveChapter(Chapter chapter) {
        chapterRepo.save(chapter);
    }

    public List<Chapter> findAllChaptersByPart(Part part) {
        return chapterRepo.findAllByPart(part);
    }

    public List<Part> findAllParts() {
        return partRepo.findAll();
    }

    public Part findPartByPartName(String partName) {
        return partRepo.findByName(partName);
    }

    public List<Question> findQuestionsByChapter(Chapter chapter) {
        return questionRepo.findAllByChapter(chapter);
    }

    public Chapter findChapterByName(String messageText) {
        return chapterRepo.findByName(messageText);
    }

    public List<Chapter> findAllChaptersByPartId(Long selectedPartId) {
        Optional<Part> byId = partRepo.findById(selectedPartId);
        if (byId.isPresent()) {
            return byId.get().getChapter();
        } else {
            return new ArrayList<>();
        }
    }

    @Cacheable(value = "chapter_questions")
    public List<Question> findAllQuestsByChapterId(Long id) {
        List<Question> allByChapterId = questionRepo.findAllByChapterId(id);
        if (allByChapterId == null) {
            return new ArrayList<>();
        } else {
            return allByChapterId;
        }
    }

    @CacheEvict(allEntries = true, value = {"chapter_questions", "question"})
    public void evictAllQuestions() {
    }

    @Cacheable(value = "question")
    public Question getQuestionById(Long id) {
        Optional<Question> byId = questionRepo.findById(id);
        return byId.orElse(null);
    }


    public Question getNextQuest(User user) {
        List<Question> allQuestsByChapterId = findAllQuestsByChapterId(user.getSelectedChapterId());
        Collections.shuffle(allQuestsByChapterId);
        Question question = null;

        if (user.getBotState() == BotState.LEARNING) {
            question = allQuestsByChapterId.size() > 0 ? allQuestsByChapterId.get(0) : null;
        } else {
            List<Question> availableQuestsForTesting = allQuestsByChapterId.stream().filter(e -> !user.getSolvedQuestions().contains(e.getId()) && !user.getSelectedQuestionId().equals(e.getId())).collect(Collectors.toList());
            question = availableQuestsForTesting.size() > 0 ? availableQuestsForTesting.get(0) : null;
        }

        return question;
    }

    public void removeQuestionById(Long id) {
        Question q = getQuestionById(id);
        if (q != null) {
           questionRepo.delete(q);
           evictAllQuestions();
        }
     }

}
