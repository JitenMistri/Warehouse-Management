package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ArchiveWarehouseUseCaseTest {

  @Test
  void shouldArchiveActiveWarehouse() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    var warehouse = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 30, 20);
    store.warehouses.add(warehouse);

    new ArchiveWarehouseUseCase(store).archive(warehouse);

    assertNotNull(warehouse.archivedAt);
  }

  @Test
  void shouldRejectNullWarehouse() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    assertThrows(WarehouseNotFoundException.class, () -> new ArchiveWarehouseUseCase(store).archive(null));
  }

  @Test
  void shouldNotChangeAlreadyArchivedWarehouse() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    var warehouse = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 30, 20);
    warehouse.archivedAt = java.time.LocalDateTime.now().minusDays(1);

    new ArchiveWarehouseUseCase(store).archive(warehouse);

    assertTrue(warehouse.archivedAt.isBefore(java.time.LocalDateTime.now().minusHours(1)));
  }
}
