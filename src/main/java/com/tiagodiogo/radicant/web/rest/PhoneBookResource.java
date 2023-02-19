package com.tiagodiogo.radicant.web.rest;

import com.tiagodiogo.radicant.domain.PhoneBookRecord;
import com.tiagodiogo.radicant.service.PhoneBookService;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/phone-book")
public class PhoneBookResource {

    private final Logger log = LoggerFactory.getLogger(PhoneBookResource.class);
    private final PhoneBookService phoneBookService;

    public PhoneBookResource(PhoneBookService phoneBookService) {
        this.phoneBookService = phoneBookService;
    }

    /**
     * {@code GET /phone-book} : Obtain all the phone book records.
     *
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and in the body a List of {@link PhoneBookRecord}
     */
    @GetMapping
    public ResponseEntity<List<PhoneBookRecord>> getPhoneRecords() {
        log.info("REST request to get all phone records");
        return ResponseEntity.ok().body(phoneBookService.getAllRecords());
    }

    /**
     * {@code GET /phone-book/{recordId}} : Fetch a single phone book record by its identifier.
     *
     * @param recordId the phone book record identifier.
     * @return the {@link ResponseEntity} with status {@code 200(OK)} and in the body the {@link PhoneBookRecord}.
     * Or, a {@link ResponseEntity} with status {@code 404(NOT_FOUND)} if no {@link PhoneBookRecord} matched the provided identifier.
     */
    @GetMapping("/{recordId}")
    public ResponseEntity<PhoneBookRecord> getPhoneRecordById(@PathVariable Long recordId) {
        log.info("REST request to get phone record by id: {}", recordId);
        return ResponseUtil.wrapOrNotFound(phoneBookService.getRecordById(recordId));
    }

    /**
     * {@code POST /phone-book} : Create a new phone book record.
     *
     * @param phoneBookRecord the record to persist.
     * @return the {@link ResponseEntity} with status {@code 201(CREATED)} and in the body the persisted {@link PhoneBookRecord}.
     * @throws URISyntaxException in the event of a badly formed URI.
     */
    @PostMapping
    public ResponseEntity<Long> createPhoneRecord(@RequestBody PhoneBookRecord phoneBookRecord) throws URISyntaxException {
        log.info("REST request to create a new phone record defined by: {}", phoneBookRecord);
        Long newRecordId = phoneBookService.addRecord(phoneBookRecord);
        return ResponseEntity.created(new URI("api/phone-book/" + newRecordId)).body(newRecordId);
    }

    /**
     * {@code PUT /phone-book} : Update an existing phone book record.
     *
     * @param phoneBookRecord the record to update.
     * @return the {@link ResponseEntity} with status {@code 204(NO_CONTENT)}.
     * Or, a {@link ResponseStatusException} with status {@code 404(NOT_FOUND)} if no {@link PhoneBookRecord} matched the provided identifier.
     */
    @PutMapping
    public ResponseEntity<Void> updatePhoneRecord(@RequestBody PhoneBookRecord phoneBookRecord) {
        log.info("REST request to update an existing phone record with id: {}", phoneBookRecord.getId());
        if (phoneBookService.updateRecord(phoneBookRecord)) {
            return ResponseEntity.noContent().build();
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    /**
     *  {@code DELETE /phone-book/{recordId}} : Delete an existing phone book record.
     *
     * @param recordId the phone book record identifier
     * @return the {@link ResponseEntity} with status {@code 204(NO_CONTENT)}.
     * Or, a {@link ResponseStatusException} with status {@code 404(NOT_FOUND)} if no {@link PhoneBookRecord} matched the provided identifier.
     */
    @DeleteMapping("/{recordId}")
    public ResponseEntity<Void> deletePhoneRecord(@PathVariable Long recordId) {
        log.info("REST request to delete an existing phone record with id: {}", recordId);
        if (phoneBookService.deleteRecord(recordId)) {
            return ResponseEntity.noContent().build();
        } else throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
}
