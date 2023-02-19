package com.tiagodiogo.radicant.service;

import com.tiagodiogo.radicant.domain.PhoneBookRecord;
import com.tiagodiogo.radicant.repository.PhoneBookDatabase;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * Service to perform CRUD operations for {@link PhoneBookRecord} contained on a CSV file database.
 * Also handles conversions from CSV strings to {@link PhoneBookRecord} entities and the other way around.
 */
@Service
public class PhoneBookService {

    public static final long SELECT_ALL = -1L;
    private static final String CSV_SEPARATOR = ",";
    private final PhoneBookDatabase phoneBookDatabase;

    public PhoneBookService(PhoneBookDatabase phoneBookDatabase) {
        this.phoneBookDatabase = phoneBookDatabase;
    }

    /**
     * Obtain all {@link PhoneBookRecord} from the CSV file database.
     * @return the List of {@link PhoneBookRecord}.
     */
    public List<PhoneBookRecord> getAllRecords() {
        List<String> rawRecords = phoneBookDatabase.select(SELECT_ALL);
        return processRawRecords(rawRecords).collect(Collectors.toList());
    }

    /**
     * Fetch a {@link PhoneBookRecord} by its identifier from the CSV file database.
     * @param id the record identifier.
     * @return an Optional with the record, if it exists, or an empty optional if no record was found for that identifier.
     */
    public Optional<PhoneBookRecord> getRecordById(Long id) {
        List<String> rawRecords = phoneBookDatabase.select(id);
        return processRawRecords(rawRecords).findFirst();
    }

    /**
     * Persist a new {@link PhoneBookRecord} into the CSV file database.
     * @param phoneBookRecord the new record to be persisted.
     * @return the new record identifier.
     */
    public Long addRecord(PhoneBookRecord phoneBookRecord) {
        return phoneBookDatabase.insert(phoneBookRecord.toCSV());
    }

    /**
     * Updates an existing {@link PhoneBookRecord} by supplying a new entity to override.
     * @param phoneBookRecord the entity holding de field to override.
     * @return true if the record was updated, false otherwise.
     */
    public boolean updateRecord(PhoneBookRecord phoneBookRecord) {
        return phoneBookDatabase.update(phoneBookRecord.getId(), phoneBookRecord.toCSV());
    }

    /**
     * Deletes an existing {@link PhoneBookRecord} by its identifier.
     * @param id the identifier of the record to delete.
     * @return true if the record was deleted, false otherwise.
     */
    public boolean deleteRecord(Long id) {
        return phoneBookDatabase.delete(id);
    }

    /**
     * Converts a List of raw String records into a stream of PhoneBookRecords.
     * @param rawRecords a list of comma separated strings that represent a raw phone book record.
     * @return a Stream of {@link PhoneBookRecord} obtained from the @rawRecords.
     */
    private Stream<PhoneBookRecord> processRawRecords(List<String> rawRecords) {
        return rawRecords.stream().map(s -> s.split(CSV_SEPARATOR)).map(PhoneBookRecord::new);
    }
}
