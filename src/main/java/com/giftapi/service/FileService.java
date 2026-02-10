package com.giftapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

	private static final String INSERT_SQL =
			"INSERT INTO child (first_name, last_name, birth_date) VALUES (?, ?, ?)";

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	// 1 sec na 10000 wczytywania
	// 250 mb pamiec aplikacji
	// docelowo na pliku 15mln
	private final JdbcTemplate jdbcTemplate;

	public void upload(MultipartFile file) {
		int batchSize = 10_000;

		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVParser csvParser = buildParser(reader)) {

			List<String> firstNames = new ArrayList<>(batchSize);
			List<String> lastNames = new ArrayList<>(batchSize);
			List<Date> birthDates = new ArrayList<>(batchSize);

			long totalRows = 0L;
			long batchNo = 0L;
			long overallStart = System.nanoTime();

			for (CSVRecord record : csvParser) {
				String firstName = record.get("firstname");
				String lastName = record.get("lastname");
				String birthDateStr = record.get("birthDate");

				LocalDate birthDate = LocalDate.parse(birthDateStr, dateFormatter);
				Date sqlDate = Date.valueOf(birthDate);

				firstNames.add(firstName);
				lastNames.add(lastName);
				birthDates.add(sqlDate);

				if (firstNames.size() >= batchSize) {
					batchNo++;
					totalRows += executeAndLogBatch(firstNames, lastNames, birthDates, batchNo);

					firstNames.clear();
					lastNames.clear();
					birthDates.clear();
				}
			}

			if (!firstNames.isEmpty()) {
				batchNo++;
				totalRows += executeAndLogBatch(firstNames, lastNames, birthDates, batchNo);
			}

			Duration overall = Duration.ofNanos(System.nanoTime() - overallStart);
			log.info("CSV upload finished: totalRows={}, totalTimeMs={}", totalRows, overall.toMillis());

		} catch (Exception e) {
			throw new RuntimeException("Error processing file", e);
		}
	}

	private long executeAndLogBatch(
			List<String> firstNames,
			List<String> lastNames,
			List<Date> birthDates,
			long batchNo
	) {
		long start = System.nanoTime();

		jdbcTemplate.batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, firstNames.get(i));
				ps.setString(2, lastNames.get(i));
				ps.setDate(3, birthDates.get(i));
			}

			@Override
			public int getBatchSize() {
				return firstNames.size();
			}
		});

		long tookNanos = System.nanoTime() - start;

		long rows = firstNames.size();
		Duration took = Duration.ofNanos(tookNanos);
		double seconds = Math.max(tookNanos / 1_000_000_000.0, 1e-9);
		long rowsPerSec = Math.round(rows / seconds);

		log.info(
				"CSV batch {} inserted: rows={}, tookMs={}, throughputRowsPerSec={}",
				batchNo,
				rows,
				took.toMillis(),
				rowsPerSec
		);

		return rows;
	}

	private static CSVParser buildParser(Reader reader) throws IOException {
		return CSVFormat.DEFAULT.builder()
				.setHeader()
				.setSkipHeaderRecord(true)
				.setIgnoreHeaderCase(true)
				.setTrim(true)
				.build()
				.parse(reader);
	}
}
