package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseValidator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreateWarehouseUseCaseTest {

    @Test
    void shouldCreateValidWarehouse() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();
        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        Warehouse warehouse =
                WarehouseUseCaseTestSupport.warehouse(
                        "MWH.100",
                        "AMSTERDAM-002",
                        30,
                        20);

        useCase.create(warehouse);

        assertEquals(1, store.warehouses.size());
        assertNotNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }

    @Test
    void shouldRejectDuplicateBusinessUnitCode() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

        store.warehouses.add(
                WarehouseUseCaseTestSupport.warehouse(
                        "MWH.100",
                        "AMSTERDAM-002",
                        20,
                        10));

        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        var error = assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(
                        WarehouseUseCaseTestSupport.warehouse(
                                "MWH.100",
                                "AMSTERDAM-002",
                                20,
                                10)));

        assertTrue(error.getMessage().contains("already exists"));
    }

    @Test
    void shouldRejectInvalidLocation() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(
                        WarehouseUseCaseTestSupport.warehouse(
                                "MWH.100",
                                "INVALID",
                                20,
                                10)));
    }

    @Test
    void shouldRejectStockGreaterThanCapacity() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(
                        WarehouseUseCaseTestSupport.warehouse(
                                "MWH.100",
                                "AMSTERDAM-002",
                                10,
                                11)));
    }

    @Test
    void shouldRejectWhenLocationWarehouseLimitIsReached() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

        store.warehouses.add(
                WarehouseUseCaseTestSupport.warehouse(
                        "MWH.001",
                        "ZWOLLE-001",
                        20,
                        10));

        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(
                        WarehouseUseCaseTestSupport.warehouse(
                                "MWH.002",
                                "ZWOLLE-001",
                                10,
                                5)));
    }

    @Test
    void shouldRejectWhenLocationCapacityIsExceeded() {
        var store = new WarehouseUseCaseTestSupport.InMemoryWarehouseStore();

        store.warehouses.add(
                WarehouseUseCaseTestSupport.warehouse(
                        "MWH.001",
                        "AMSTERDAM-002",
                        60,
                        10));

        var validator = new WarehouseValidator(
                store,
                WarehouseUseCaseTestSupport.locations());

        var useCase = new CreateWarehouseUseCase(store, validator);

        assertThrows(
                WarehouseValidationException.class,
                () -> useCase.create(
                        WarehouseUseCaseTestSupport.warehouse(
                                "MWH.002",
                                "AMSTERDAM-002",
                                20,
                                10)));
    }
}