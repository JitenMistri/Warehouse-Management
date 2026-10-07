package com.fulfilment.application.monolith.stores;

import java.util.List;

import com.fulfilment.application.monolith.stores.events.StoreCreatedEvent;
import com.fulfilment.application.monolith.stores.events.StoreUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/store")
public class StoreResource {

    private final StoreRepository storeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public StoreResource(StoreRepository storeRepository,
                         ApplicationEventPublisher eventPublisher) {
        this.storeRepository = storeRepository;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping
    public List<Store> get() {
        return storeRepository.findAll(
                org.springframework.data.domain.Sort.by("name"));
    }

    @GetMapping("/{id}")
    public Store getSingle(@PathVariable Long id) {
        return storeRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Store with id of " + id + " does not exist.",
                                404));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Store> create(@RequestBody Store store) {

        if (store.id != null) {
            throw new ApiException(
                    "Id was invalidly set on request.", 422);
        }

        Store savedStore = storeRepository.save(store);
        eventPublisher.publishEvent(
                new StoreCreatedEvent(savedStore));
        return ResponseEntity.status(201).body(savedStore);
    }

    @PutMapping("/{id}")
    @Transactional
    public Store update(@PathVariable Long id,
                        @RequestBody Store updatedStore) {

        if (updatedStore.name == null) {
            throw new ApiException(
                    "Store Name was not set on request.", 422);
        }
        Store entity = storeRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Store with id of " + id + " does not exist.",
                                404));

        entity.name = updatedStore.name;
        entity.quantityProductsInStock =
                updatedStore.quantityProductsInStock;
        eventPublisher.publishEvent(
                new StoreUpdatedEvent(entity));
        return entity;
    }

    @PatchMapping("/{id}")
    @Transactional
    public Store patch(@PathVariable Long id,
                       @RequestBody Store updatedStore) {

        if (updatedStore.name == null) {
            throw new ApiException(
                    "Store Name was not set on request.", 422);
        }

        Store entity = storeRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Store with id of " + id + " does not exist.",
                                404));

        if (entity.name != null) {
            entity.name = updatedStore.name;
        }

        if (entity.quantityProductsInStock != 0) {
            entity.quantityProductsInStock =
                    updatedStore.quantityProductsInStock;
        }

        eventPublisher.publishEvent(
                new StoreUpdatedEvent(entity));
        return entity;
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> delete(@PathVariable Long id) {

        Store entity = storeRepository.findById(id)
                .orElseThrow(() ->
                        new ApiException(
                                "Store with id of " + id + " does not exist.",
                                404));

        storeRepository.delete(entity);
        return ResponseEntity.noContent().build();
    }

    public static class ApiException extends RuntimeException {

        private final int status;

        public ApiException(String message, int status) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}