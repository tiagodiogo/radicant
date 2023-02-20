package com.tiagodiogo.radicant.repository;

import com.tiagodiogo.radicant.domain.IDatabase;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

@Repository
public class PhoneBookDatabase implements IDatabase {

    private static final String FILE_NAME = "/tmp/phone-book.csv";
    private static final String CSV_SEPARATOR = ",";
    private static final Long SELECT_ALL = -1L;

    private final Logger log = LoggerFactory.getLogger(PhoneBookDatabase.class);

    private final Path filePath;
    private final Lock readLock;
    private final Lock writeLock;

    /**
     * Initializes the read/write locks and ensures the target file exists.
     * @throws IOException in the event of an error creating the file.
     */
    public PhoneBookDatabase() throws IOException {
        filePath = Paths.get(FILE_NAME);

        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();

        if (Files.notExists(filePath)) {
            Files.createFile(filePath);
        }
    }

    /**
     * Fetches rows from the CSV file based on the received identifier.
     * @param id can be either the row identifier or -1L representing a request to fetch all rows.
     * @return a List of comma separated values representing CSV file rows.
     */
    @Override
    public List<String> select(Long id) {
        List<String> rows = new ArrayList<>();

        readLock.lock();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile(), Charset.defaultCharset()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (id.equals(SELECT_ALL)) {
                    rows.add(line);
                } else {
                    String[] parts = line.split(CSV_SEPARATOR);
                    if (Long.valueOf(parts[0]).equals(id)) {
                        log.debug("Found existing record for id: {}", id);
                        rows.add(line);
                        return rows;
                    }
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            readLock.unlock();
        }

        return rows;
    }

    /**
     * Generates a unique identifier and persists a new row with it into the CSV file.
     * @param row the comma separated values representing a new row.
     * @return the generated row identifier.
     */
    @Override
    public Long insert(String row) {
        // Handle unique id generation
        Long uniqueID = UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE;
        String[] parts = row.split(CSV_SEPARATOR);
        parts[0] = String.valueOf(uniqueID);
        row = String.join(CSV_SEPARATOR, parts);

        // Write to File
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), Charset.defaultCharset(), true))) {
            writer.write(row);
            writer.newLine();
            log.debug("Inserted new record: {}", row);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            writeLock.unlock();
        }
        return uniqueID;
    }

    /**
     * Updates the row that matches the received identifier if one is found on the CSV file.
     * @param id the row identifier.
     * @param newRow the comma separated values to be persisted.
     * @return true if the record was updated, false otherwise.
     */
    @Override
    public boolean update(Long id, String newRow) {
        List<String> entities = select(SELECT_ALL);
        boolean updated = false;
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), Charset.defaultCharset()))) {
            for (String row : entities) {
                String[] parts = row.split(CSV_SEPARATOR);
                if (Long.valueOf(parts[0]).equals(id)) {
                    String[] newParts = newRow.split(CSV_SEPARATOR);
                    writer.write(id + "," + newParts[1] + "," + newParts[2] + "," + newParts[3]);
                    updated = true;
                    log.debug("Updated existing record with id: {}", id);
                } else {
                    writer.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3]);
                }
                writer.newLine();
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            writeLock.unlock();
        }
        return updated;
    }

    /**
     * Deletes the row that matches the received identifier if one is found on the CSV file.
     * @param id the row identifier.
     * @return true if the record was deleted, false otherwise.
     */
    @Override
    public boolean delete(Long id) {
        List<String> entities = select(SELECT_ALL);
        boolean deleted = false;
        writeLock.lock();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), Charset.defaultCharset()))) {
            for (String row : entities) {
                String[] parts = row.split(CSV_SEPARATOR);
                if (!Long.valueOf(parts[0]).equals(id)) {
                    writer.write(parts[0] + "," + parts[1] + "," + parts[2] + "," + parts[3]);
                    writer.newLine();
                } else {
                    deleted = true;
                    log.debug("Deleted existing record with id: {}", id);
                }
            }
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            writeLock.unlock();
        }
        return deleted;
    }
}
