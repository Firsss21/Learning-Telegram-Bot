package ru.firsov.study.Java.Telegram.Bot.common.cache;

import com.google.common.cache.CacheBuilder;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.firsov.study.Java.Telegram.Bot.common.entity.User;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Component
public class UserCache {

//    private CacheManager cacheManager;
//
//    public static final String NAME = "user";

//    @Scheduled(fixedDelay = 10000L)
//    @PostConstruct
//    @SneakyThrows
//    public void cache() {
//        var cachedUsers = (ConcurrentHashMap<Long, User>) cacheManager.getCache(NAME).getNativeCache();
//
//        for (var user : cachedUsers.values()) {
//            cacheManager.getCache(NAME).evictIfPresent()
//            System.out.println("User[" + user.getChatId() + "] " + user.getName() + " state: " + user.getBotState().name() + " (" + user.getBotStateVariable() + ")");
//        }
//    }
}
