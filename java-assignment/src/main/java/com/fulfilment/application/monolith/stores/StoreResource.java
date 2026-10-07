package com.fulfilment.application.monolith.stores;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/store")
public class StoreResource {

  private final StoreRepository storeRepository;
  private final LegacyStoreManagerGateway legacyStoreManagerGateway;

  public StoreResource(StoreRepository storeRepository, LegacyStoreManagerGateway legacyStoreManagerGateway) {
    this.storeRepository = storeRepository;
    this.legacyStoreManagerGateway = legacyStoreManagerGateway;
  }

  @GetMapping
  public List<Store> get() {
    return storeRepository.findAll(org.springframework.data.domain.Sort.by("name"));
  }

  @GetMapping("/{id}")
  public Store getSingle(@PathVariable Long id) {
    return storeRepository.findById(id)
        .orElseThrow(() -> new ApiException("Store with id of " + id + " does not exist.", 404));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<Store> create(@RequestBody Store store) {
    if (store.id != null) {
      throw new ApiException("Id was invalidly set on request.", 422);
    }
    storeRepository.save(store);
    invokeLegacyAfterCommit(() -> legacyStoreManagerGateway.createStoreOnLegacySystem(store));
    return ResponseEntity.status(201).body(store);
  }

  @PutMapping("/{id}")
  @Transactional
  public Store update(@PathVariable Long id, @RequestBody Store updatedStore) {
    if (updatedStore.name == null) {
      throw new ApiException("Store Name was not set on request.", 422);
    }
    Store entity = storeRepository.findById(id)
        .orElseThrow(() -> new ApiException("Store with id of " + id + " does not exist.", 404));
    entity.name = updatedStore.name;
    entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    invokeLegacyAfterCommit(() -> legacyStoreManagerGateway.updateStoreOnLegacySystem(updatedStore));
    return entity;
  }

  @PatchMapping("/{id}")
  @Transactional
  public Store patch(@PathVariable Long id, @RequestBody Store updatedStore) {
    if (updatedStore.name == null) {
      throw new ApiException("Store Name was not set on request.", 422);
    }
    Store entity = storeRepository.findById(id)
        .orElseThrow(() -> new ApiException("Store with id of " + id + " does not exist.", 404));
    if (entity.name != null) {
      entity.name = updatedStore.name;
    }
    if (entity.quantityProductsInStock != 0) {
      entity.quantityProductsInStock = updatedStore.quantityProductsInStock;
    }
    invokeLegacyAfterCommit(() -> legacyStoreManagerGateway.updateStoreOnLegacySystem(updatedStore));
    return entity;
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Store entity = storeRepository.findById(id)
        .orElseThrow(() -> new ApiException("Store with id of " + id + " does not exist.", 404));
    storeRepository.delete(entity);
    return ResponseEntity.noContent().build();
  }

  private void invokeLegacyAfterCommit(Runnable action) {
    TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
      @Override
      public void afterCommit() {
        action.run();
      }
    });
  }

  public static class ApiException extends RuntimeException {
    private final int status;
    public ApiException(String message, int status) {
      super(message); this.status = status;
    }
    public int getStatus() {
      return status;
    }
  }
}
