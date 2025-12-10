package com.giftapi.scheduler;

import com.giftapi.service.ExpensiveGiftNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "scheduler.expensive.gifts.enabled", havingValue = "true", matchIfMissing = true)
public class ExpensiveGiftScheduler {

	private final ExpensiveGiftNotificationService notificationService;

	@Scheduled(cron = "${scheduler.expensive.gifts.cron}")
	public void scheduleExpensiveGiftsProcessing() {
		log.info("=== Triggered scheduled expensive gifts processing ===");
		try {
			notificationService.processExpensiveGifts();
			log.info("=== Scheduled processing completed successfully ===");
		} catch (Exception e) {
			log.error("=== Scheduled processing failed ===", e);
		}
	}
}
