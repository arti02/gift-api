package com.giftapi.datageneration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class TestDataGeneration {

	private final JdbcTemplate jdbcTemplate;

	@Scheduled(cron = "0 3 13 * * *")
	@Transactional
	public void generateTestData() {
		log.info("=== Triggered scheduled data generation ===");
		int batchSize = 10000;

		for (int batch = 0; batch < 75; batch++) {
			insertBatch(batchSize);
			log.info("Inserted batch {}/75", batch + 1);
		}
	}

	private void insertBatch(int size) {
		jdbcTemplate.batchUpdate(
				"INSERT INTO child (first_name, last_name, birth_date, child_type, favorite_sport, dress_color) VALUES (?, ?, ?, ?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						boolean isBoy = i % 2 == 0;
						ps.setString(1, isBoy ? "John" : "Mary");
						ps.setString(2, isBoy ? "Doe" : "Jane");
						ps.setDate(3, Date.valueOf(isBoy ? "2015-05-10" : "2016-03-15"));
						ps.setString(4, isBoy ? "BOY" : "GIRL");
						ps.setString(5, isBoy ? "Football" : null);
						ps.setString(6, isBoy ? null : "Pink");
					}

					@Override
					public int getBatchSize() {
						return size;
					}
				}
		);

		Long maxChildId = jdbcTemplate.queryForObject("SELECT MAX(id) FROM child", Long.class);
		Long minChildId = maxChildId - size + 1;

		jdbcTemplate.batchUpdate(
				"INSERT INTO gift (name, price, child_id) VALUES (?, ?, ?)",
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						long childId = minChildId + (i / 2); // 2 gifts per child
						boolean isFirstGift = i % 2 == 0;

						ps.setString(1, isFirstGift ? "Toy Car" : "Doll");
						ps.setBigDecimal(2, randomBigDecimal());
						ps.setLong(3, childId);
					}

					@Override
					public int getBatchSize() {
						return size * 2;
					}
				}
		);
	}

	private BigDecimal randomBigDecimal() {
		Random random = new Random();
		BigDecimal min = new BigDecimal("101");
		BigDecimal max = new BigDecimal("1000");
		BigDecimal range = max.subtract(min);
		BigDecimal fraction = new BigDecimal(random.nextDouble());
		return min.add(range.multiply(fraction)).setScale(2, RoundingMode.HALF_UP);
	}


}
