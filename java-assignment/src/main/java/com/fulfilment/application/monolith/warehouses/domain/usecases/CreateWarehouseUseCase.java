package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public CreateWarehouseUseCase(
          WarehouseStore warehouseStore,
          WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void create(Warehouse warehouse) {

    warehouseValidator.validateForCreation(warehouse);

    if (warehouse.createdAt == null) {
      warehouse.createdAt = LocalDateTime.now();
    }

    warehouse.archivedAt = null;

    warehouseStore.create(warehouse);
  }
}