package com.giftapi.service;

import com.giftapi.exception.GiftApiException;
import com.giftapi.model.entity.Child;
import com.giftapi.model.entity.Gift;
import com.giftapi.repository.ChildRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpensiveGiftNotificationService {

	@Value("${gift.notification.price-threshold:100.00}")
	private BigDecimal priceThreshold;

	@Value("${gift.notification.batch-size:100}")
	private int batchSize;

	private static final String LOG_PATTERN = "{0} {1} - [{2}]";

	private final ChildRepository childRepository;


	@Transactional(readOnly = true)
	public void processExpensiveGifts() {
		log.info("Starting expensive gifts processing (threshold: {} PLN)...", priceThreshold);

		AtomicInteger processedCount = new AtomicInteger(0);
		long startTime = System.currentTimeMillis();

		try (Stream<Child> childStream = childRepository.streamChildrenWithExpensiveGifts(priceThreshold)) {
			childStream.forEach(child -> {
				logExpensiveGifts(child);

				int count = processedCount.incrementAndGet();
				if (count % batchSize == 0) {
					log.debug("Processed {} children so far...", count);
				}
			});
		} catch (Exception e) {
			throw GiftApiException.of("Failed to process expensive gifts", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		long duration = System.currentTimeMillis() - startTime;
		log.info("Finished processing {} children with expensive gifts in {} ms",
				processedCount.get(), duration);
	}

	private void logExpensiveGifts(Child child) {
		List<String> expensiveGifts = child.getGifts().stream()
				.filter(gift -> gift.getPrice().compareTo(priceThreshold) > 0)
				.map(this::formatGift)
				.collect(Collectors.toList());

		if (!expensiveGifts.isEmpty()) {
			String message = MessageFormat.format(
					LOG_PATTERN,
					child.getFirstName(),
					child.getLastName(),
					String.join(", ", expensiveGifts)
			);
			log.info(message);
		}
	}

	private String formatGift(Gift gift) {
		return MessageFormat.format("{0} {1}PLN", gift.getName(), gift.getPrice());
	}
}

