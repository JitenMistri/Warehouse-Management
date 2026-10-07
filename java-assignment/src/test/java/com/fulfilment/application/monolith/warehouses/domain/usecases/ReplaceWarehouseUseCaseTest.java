package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReplaceWarehouseUseCaseTest {

  @Test
  void shouldArchiveOldWarehouseAndCreateReplacement() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

    var current =
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    30,
                    20);

    store.warehouses.add(current);

    var replacement =
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    40,
                    20);

    var validator = new WarehouseValidator(
            store,
            WarehouseUseCaseTestSupport.locations());

    var useCase = new ReplaceWarehouseUseCase(
            store,
            validator);

    useCase.replace(replacement);

    assertNotNull(current.archivedAt);
    assertSame(
            replacement,
            store.findByBusinessUnitCode("MWH.100"));
    assertNotNull(replacement.createdAt);
  }

  @Test
  void shouldRejectReplacementWhenWarehouseDoesNotExist() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

    var replacement =
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    40,
                    20);

    var validator = new WarehouseValidator(
            store,
            WarehouseUseCaseTestSupport.locations());

    var useCase = new ReplaceWarehouseUseCase(
            store,
            validator);

    assertThrows(
            WarehouseNotFoundException.class,
            () -> useCase.replace(replacement));
  }

  @Test
  void shouldRejectWhenStockDoesNotMatch() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

    store.warehouses.add(
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    30,
                    20));

    var replacement =
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    40,
                    19);

    var validator = new WarehouseValidator(
            store,
            WarehouseUseCaseTestSupport.locations());

    var useCase = new ReplaceWarehouseUseCase(
            store,
            validator);

    assertThrows(
            WarehouseValidationException.class,
            () -> useCase.replace(replacement));
  }

  @Test
  void shouldRejectWhenReplacementCapacityCannotAccommodateStock() {
    var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

    store.warehouses.add(
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    30,
                    20));

    var replacement =
            WarehouseUseCaseTestSupport.warehouse(
                    "MWH.100",
                    "AMSTERDAM-002",
                    19,
                    20);

    var validator = new WarehouseValidator(
            store,
            WarehouseUseCaseTestSupport.locations());

    var useCase = new ReplaceWarehouseUseCase(
            store,
            validator);

    assertThrows(
            WarehouseValidationException.class,
            () -> useCase.replace(replacement));
  }
}