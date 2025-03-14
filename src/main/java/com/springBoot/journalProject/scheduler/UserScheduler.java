package com.springBoot.journalProject.scheduler;

import com.springBoot.journalProject.cache.AppCache;
import com.springBoot.journalProject.entity.PostEntry;
import com.springBoot.journalProject.entity.User;
import com.springBoot.journalProject.enums.Sentiment;
import com.springBoot.journalProject.repository.UserRepositoryImpl;
import com.springBoot.journalProject.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class UserScheduler {

    private final EmailService emailService;
    private final UserRepositoryImpl userRepository;
    private final AppCache appCache;

    @Scheduled(cron = "0 0 9 * * SUN")
    public void fetchUsersAndSendSaMail() {
        List<User> users = userRepository.getUserForSA();
        for (User user : users) {
            List<PostEntry> journalEntries = user.getEntry();
            List<Sentiment> sentiments = journalEntries.stream().filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null)
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
            }
            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }
            if (mostFrequentSentiment != null) {
                emailService.sendMail(user.getEmail(),"Sentiment for last 7 days ", mostFrequentSentiment.toString(),"kimybh44@gmail.com");
            }
        }
    }

    @Scheduled(cron = "0 0/1 0 ? * * ")
    public void clearAppCache()
    {
        appCache.init();
    }
}
