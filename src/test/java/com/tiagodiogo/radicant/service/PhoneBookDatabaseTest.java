package com.tiagodiogo.radicant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.tiagodiogo.radicant.domain.PhoneBookRecord;
import com.tiagodiogo.radicant.repository.PhoneBookDatabase;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class PhoneBookDatabaseTest {

    private static final String FILE_NAME = "/tmp/phone-book.csv";
    private Path filePath;

    PhoneBookRecord alice = new PhoneBookRecord(123L, "Alice", "alice@gmail.com", 210063423);
    PhoneBookRecord bob = new PhoneBookRecord(456L, "Bob", "bob@gmail.com", 210063423);
    PhoneBookRecord mallory = new PhoneBookRecord(789L, "Mallory", "mallory@gmail.com", 210063423);
    List<PhoneBookRecord> records = Arrays.asList(alice, bob);

    @InjectMocks
    private PhoneBookDatabase phoneBookDatabase;

    @BeforeAll
    void initialSetup() {
        filePath = Paths.get(FILE_NAME);
    }

    @BeforeEach
    void populateFile() throws IOException {
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }
        Files.createFile(filePath);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), Charset.defaultCharset(), true))) {
            for (PhoneBookRecord record : records) {
                writer.write(record.toCSV());
                writer.newLine();
            }
        }
    }

    @Test
    void testSelectAll() {
        List<String> records = phoneBookDatabase.select(-1L);
        assertThat(records).hasSize(2).contains(alice.toCSV(), bob.toCSV());
    }

    @Test
    void testSelectByIdExists() {
        List<String> records = phoneBookDatabase.select(alice.getId());
        assertThat(records).hasSize(1).contains(alice.toCSV());
    }

    @Test
    void testSelectByIdNotExists() {
        List<String> records = phoneBookDatabase.select(mallory.getId());
        assertThat(records).isEmpty();
    }

    @Test
    void testInsertReturn() {
        Long newRecordId = phoneBookDatabase.insert(mallory.toCSV());
        assertThat(newRecordId).isNotNull();
    }

    @Test
    void testSelectAllAfterInsert() {
        mallory.setId(phoneBookDatabase.insert(mallory.toCSV()));
        List<String> records = phoneBookDatabase.select(-1L);
        assertThat(records).hasSize(3).contains(alice.toCSV(), bob.toCSV(), mallory.toCSV());
    }

    @Test
    void testSelectByIdAfterInsert() {
        mallory.setId(phoneBookDatabase.insert(mallory.toCSV()));
        List<String> records = phoneBookDatabase.select(mallory.getId());
        assertThat(records).hasSize(1).contains(mallory.toCSV());
    }

    @Test
    void testUpdateWhenIdExists() {
        Boolean updated = phoneBookDatabase.update(alice.getId(), mallory.toCSV());
        PhoneBookRecord updatedAlice = new PhoneBookRecord(alice.getId(), mallory.getName(), mallory.getEmail(), mallory.getMobile());
        assertThat(updated).isTrue();
        List<String> records = phoneBookDatabase.select(alice.getId());
        assertThat(records).hasSize(1).contains(updatedAlice.toCSV());
    }

    @Test
    void testUpdateWhenIdNotExists() {
        Boolean updated = phoneBookDatabase.update(mallory.getId(), bob.toCSV());
        assertThat(updated).isFalse();
        List<String> records = phoneBookDatabase.select(mallory.getId());
        assertThat(records).isEmpty();
    }

    @Test
    void testDeleteWhenIdExists() {
        Boolean deleted = phoneBookDatabase.delete(alice.getId());
        assertThat(deleted).isTrue();
        List<String> records = phoneBookDatabase.select(-1L);
        assertThat(records).hasSize(1).contains(bob.toCSV());
    }

    @Test
    void testDeleteWhenIdNotExists() {
        Boolean deleted = phoneBookDatabase.delete(mallory.getId());
        assertThat(deleted).isFalse();
        List<String> records = phoneBookDatabase.select(-1L);
        assertThat(records).hasSize(2).contains(alice.toCSV(), bob.toCSV());
    }
}
