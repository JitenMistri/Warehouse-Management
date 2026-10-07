package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.validators.WarehouseValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final WarehouseValidator warehouseValidator;

  public ReplaceWarehouseUseCase(
          WarehouseStore warehouseStore,
          WarehouseValidator warehouseValidator) {
    this.warehouseStore = warehouseStore;
    this.warehouseValidator = warehouseValidator;
  }

  @Override
  public void replace(Warehouse newWarehouse) {

    if (newWarehouse == null ||
            newWarehouse.businessUnitCode == null ||
            newWarehouse.businessUnitCode.isBlank()) {
      throw new WarehouseValidationException(
              "Business unit code is required.");
    }

    Warehouse current =
            warehouseStore.findByBusinessUnitCode(
                    newWarehouse.businessUnitCode);

    if (current == null) {
      throw new WarehouseNotFoundException(
              "Warehouse with business unit code '" +
                      newWarehouse.businessUnitCode +
                      "' does not exist.");
    }

    warehouseValidator.validateForReplacement(
            newWarehouse,
            current);

    // Archive the existing warehouse to preserve history.
    current.archivedAt = LocalDateTime.now();
    warehouseStore.update(current);

    // Create the replacement warehouse using the same business unit code.
    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;

    warehouseStore.create(newWarehouse);
  }
}