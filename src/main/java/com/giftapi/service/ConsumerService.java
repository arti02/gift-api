package com.giftapi.service;

import com.giftapi.model.dto.ChildImportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@RequiredArgsConstructor
public class ConsumerService implements Runnable {

	private static final String INSERT_SQL = "INSERT INTO child (first_name, last_name, birth_date) VALUES (?, ?, ?)";

	private final BlockingQueue<List<ChildImportDTO>> queue;
	private final JdbcTemplate jdbcTemplate;
	private final AtomicLong totalRows;
	private final int consumerId;

	private static final AtomicLong batchCounter = new AtomicLong(0L);

	@Override
	public void run() {
		try {
			while (true) {
				List<ChildImportDTO> batch = queue.take();
				if (batch.isEmpty()) {
					log.info("Consumer {} finished work.", consumerId);
					break;
				}

				executeAndLogBatch(batch);
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("Consumer {} interrupted", consumerId, e);
		}
	}

	private void executeAndLogBatch(List<ChildImportDTO> batch) {
		final long batchNumber = batchCounter.incrementAndGet();
		final long start = System.nanoTime();

		//TODO:bulk insert
		jdbcTemplate.batchUpdate(
				INSERT_SQL, new BatchPreparedStatementSetter() {

					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						ChildImportDTO child = batch.get(i);
						ps.setString(1, child.getFirstName());
						ps.setString(2, child.getLastName());
						ps.setDate(3, child.getBirthDate());
					}

					@Override
					public int getBatchSize() {
						return batch.size();
					}
				});

		long tookNanos = System.nanoTime() - start;
		long rows = batch.size();
		totalRows.addAndGet(rows);

		long tookMs = tookNanos / 1_000_000;
		double seconds = Math.max(tookNanos / 1_000_000_000.0, 1e-9);
		long rowsPerSec = Math.round(rows / seconds);

		log.info(
				"Consumer {} | CSV batch {} inserted: rows={}, tookMs={}, throughputRowsPerSec={}",
				consumerId,
				batchNumber,
				rows,
				tookMs,
				rowsPerSec);
	}
}
