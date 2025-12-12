package com.giftapi.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.StringJoiner;

@Service
@Slf4j
@RequiredArgsConstructor
public class ExpensiveGiftNotificationService {

	@Value("${gift.notification.price-threshold:100.00}")
	private BigDecimal priceThreshold;

	private static final String LOG_PATTERN = "{0} {1} - [{2}]";

	private final JdbcTemplate jdbcTemplate;

	@Transactional(readOnly = true)
	public void processExpensiveGifts() {
		jdbcTemplate.setFetchSize(1000);
		log.info("Starting expensive gifts processing (threshold: {} PLN)...", priceThreshold);
		long startTime = System.currentTimeMillis();

		String sql = """
            SELECT c.id, c.first_name, c.last_name, g.name AS gift_name, g.price AS gift_price
            FROM child c
            JOIN gift g ON c.id = g.child_id
            WHERE g.price > ?
            ORDER BY c.id
            """;

		DirectLoggingProcessor processor = new DirectLoggingProcessor();
		jdbcTemplate.query(sql, processor, priceThreshold);
		processor.logFinalChild();

		long duration = System.currentTimeMillis() - startTime;
		log.info("Finished processing {} children with expensive gifts in {} ms",
				processor.getProcessedChildrenCount(), duration);
	}

	private String formatGift(String name, BigDecimal price) {
		return MessageFormat.format("{0} {1}PLN", name, price);
	}

	@Getter
	private class DirectLoggingProcessor implements RowCallbackHandler {
		private Long lastChildId;
		private String childFirstName;
		private String childLastName;
		private StringJoiner giftsJoiner = new StringJoiner(", ");
		private int processedChildrenCount = 0;

		@Override
		public void processRow(ResultSet rs) throws SQLException {
			long currentChildId = rs.getLong("id");

			if (lastChildId != null && !lastChildId.equals(currentChildId)) {
				logCurrentChild();
			}

			if (lastChildId == null || !lastChildId.equals(currentChildId)) {
				lastChildId = currentChildId;
				childFirstName = rs.getString("first_name");
				childLastName = rs.getString("last_name");
			}

			String giftName = rs.getString("gift_name");
			BigDecimal giftPrice = rs.getBigDecimal("gift_price");
			giftsJoiner.add(formatGift(giftName, giftPrice));
		}

		private void logCurrentChild() {
			if (childFirstName != null) {
				String message = MessageFormat.format(LOG_PATTERN, childFirstName, childLastName, giftsJoiner.toString());
				log.info(message);
				processedChildrenCount++;
				giftsJoiner = new StringJoiner(", ");
			}
		}

		public void logFinalChild() {
			logCurrentChild();
		}
	}
}

