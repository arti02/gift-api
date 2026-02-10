package com.giftapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.concurrent.atomic.AtomicLong;

@Service
@RequiredArgsConstructor
@Slf4j
public class FastCsvImportService {

	private final DataSource dataSource;

	public void upload(MultipartFile file) throws Exception {
		long start = System.currentTimeMillis();
		AtomicLong rowCount = new AtomicLong(0);

		try (Connection conn = dataSource.getConnection();
				InputStream is = file.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, StandardCharsets.UTF_8))) {

			BaseConnection baseConn = conn.unwrap(BaseConnection.class);
			CopyManager copyManager = new CopyManager(baseConn);

			String copySql = """
            COPY child (first_name, last_name, birth_date)
            FROM STDIN WITH (
                FORMAT CSV,
                HEADER true,
                DELIMITER ',',
                QUOTE '"',
                ESCAPE '\\',
                FORCE_NOT_NULL (first_name, last_name, birth_date)
            )
            """;

			long rows = copyManager.copyIn(copySql, reader);

			rowCount.set(rows);

		} catch (Exception e) {
			log.error("Błąd podczas COPY z pliku CSV", e);
			throw e;
		}

		long tookMs = System.currentTimeMillis() - start;
		log.info(
				"COPY zakończony | rows: {} | czas: {} ms",
				rowCount.get(),
				tookMs);
	}
}