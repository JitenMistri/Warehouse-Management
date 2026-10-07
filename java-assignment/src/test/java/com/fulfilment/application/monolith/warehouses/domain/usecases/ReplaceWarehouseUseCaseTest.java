package com.fulfilment.application.monolith.warehouses.domain.usecases;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ReplaceWarehouseUseCaseTest {

  @Test
  void shouldArchiveOldWarehouseAndCreateReplacement() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    var current = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 30, 20);
    store.warehouses.add(current);
    var replacement = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 40, 20);

    new ReplaceWarehouseUseCase(store, WarehouseUseCaseTestSupport.locations()).replace(replacement);

    assertNotNull(current.archivedAt);
    assertSame(replacement, store.findByBusinessUnitCode("MWH.100"));
    assertNotNull(replacement.createdAt);
  }

  @Test
  void shouldRejectReplacementWhenWarehouseDoesNotExist() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    var replacement = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 40, 20);

    assertThrows(WarehouseNotFoundException.class,
        () -> new ReplaceWarehouseUseCase(store, WarehouseUseCaseTestSupport.locations()).replace(replacement));
  }

  @Test
  void shouldRejectWhenStockDoesNotMatch() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    store.warehouses.add(WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 30, 20));
    var replacement = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 40, 19);

    assertThrows(WarehouseValidationException.class,
        () -> new ReplaceWarehouseUseCase(store, WarehouseUseCaseTestSupport.locations()).replace(replacement));
  }

  @Test
  void shouldRejectWhenReplacementCapacityCannotAccommodateStock() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
    store.warehouses.add(WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 30, 20));
    var replacement = WarehouseUseCaseTestSupport.warehouse("MWH.100", "AMSTERDAM-002", 19, 20);

    assertThrows(WarehouseValidationException.class,
        () -> new ReplaceWarehouseUseCase(store, WarehouseUseCaseTestSupport.locations()).replace(replacement));
  }
}
