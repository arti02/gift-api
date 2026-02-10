package com.giftapi.service;

import com.giftapi.model.dto.ChildImportDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Date;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.concurrent.Executors.newFixedThreadPool;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileServiceMoreTheOneThread {

	// --- Konfiguracja wielowątkowości ---
	private final static int CONSUMER_THREADS =
			Math.min(Runtime.getRuntime().availableProcessors() * 2 + 2, 16); // Liczba wątków zapisujących do bazy
	private final static int BATCH_SIZE = 10_000; // Liczba rekordów w jednej paczce przekazywanej do kolejki
	private final static int QUEUE_CAPACITY = 10; // Pojemność kolejki (bufor), aby nie zapełnić pamięci RAM

	private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private final JdbcTemplate jdbcTemplate;

	public void upload(MultipartFile file) throws InterruptedException {
		// Blokująca kolejka do komunikacji między producentem a konsumentami
		BlockingQueue<List<ChildImportDTO>> queue = new ArrayBlockingQueue<>(QUEUE_CAPACITY);
		System.out.println(CONSUMER_THREADS);
		// Pula wątków dla konsumentów
		ExecutorService consumerExecutor = newFixedThreadPool(CONSUMER_THREADS);

		long overallStart = System.nanoTime();
		AtomicLong totalRows = new AtomicLong(0);

		// --- Uruchomienie Konsumentów ---
		for (int i = 0; i < CONSUMER_THREADS; i++) {
			consumerExecutor.submit(new ConsumerService(queue, jdbcTemplate, totalRows, i + 1));
		}

		// --- Logika Producenta (w głównym wątku) ---
		try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
				CSVParser csvParser = buildParser(reader)) {

			List<ChildImportDTO> batch = new ArrayList<>(BATCH_SIZE);

			for (CSVRecord record : csvParser) {
				String firstName = record.get("firstname");
				String lastName = record.get("lastname");
				String birthDateStr = record.get("birthDate");

				LocalDate birthDate = LocalDate.parse(birthDateStr, dateFormatter);
				Date sqlDate = Date.valueOf(birthDate);

				batch.add(new ChildImportDTO(firstName, lastName, sqlDate));

				if (batch.size() >= BATCH_SIZE) {
					queue.put(batch); // Blokuje, jeśli kolejka jest pełna
					batch = new ArrayList<>(BATCH_SIZE);
				}
			}

			// Dodaj ostatnią, niepełną paczkę
			if (!batch.isEmpty()) {
				queue.put(batch);
			}

		} catch (IOException | InterruptedException e) {
			// W razie błędu przerwij wszystko
			consumerExecutor.shutdownNow();
			throw new RuntimeException("Error processing file", e);
		} finally {
			// --- Koordynacja zamknięcia ---
			// Sygnalizujemy konsumentom, że producent zakończył pracę, wysyłając puste listy
			for (int i = 0; i < CONSUMER_THREADS; i++) {
				queue.put(new ArrayList<>()); // Pusta lista jest sygnałem końca ("poison pill")
			}
		}

		consumerExecutor.shutdown();

		Duration overall = Duration.ofNanos(System.nanoTime() - overallStart);
		log.info("CSV upload finished: totalRows={}, totalTimeMs={}", totalRows.get(), overall.toMillis());
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