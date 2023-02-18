package com.tiagodiogo.radicant.service;

import com.tiagodiogo.radicant.domain.PhoneBookRecord;
import com.tiagodiogo.radicant.repository.PhoneBookDatabase;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PhoneBookService {

    private static final String CSV_SEPARATOR = ",";
    private final Logger log = LoggerFactory.getLogger(PhoneBookService.class);
    private final PhoneBookDatabase phoneBookDatabase;

    public PhoneBookService(PhoneBookDatabase phoneBookDatabase) {
        this.phoneBookDatabase = phoneBookDatabase;
    }

    public List<PhoneBookRecord> getAllRecords() {
        List<String> rawRecords = phoneBookDatabase.select(-1L);
        return processRawRecords(rawRecords).collect(Collectors.toList());
    }

    public Optional<PhoneBookRecord> getRecordById(Long id) {
        List<String> rawRecords = phoneBookDatabase.select(id);
        return processRawRecords(rawRecords).findFirst();
    }

    public Long addRecord(PhoneBookRecord phoneBookRecord) {
        return phoneBookDatabase.insert(phoneBookRecord.toCSV());
    }

    public boolean updateRecord(PhoneBookRecord phoneBookRecord) {
        return phoneBookDatabase.update(phoneBookRecord.getId(), phoneBookRecord.toCSV());
    }

    public boolean deleteRecord(Long id) {
        return phoneBookDatabase.delete(id);
    }

    private Stream<PhoneBookRecord> processRawRecords(List<String> rawRecords) {
        return rawRecords.stream().map(s -> s.split(CSV_SEPARATOR)).map(PhoneBookRecord::new);
    }
}
