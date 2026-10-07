package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.springframework.stereotype.Service;

@Service
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void create(Warehouse warehouse) {
    validateWarehouse(warehouse);

    if (warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode) != null) {
      throw new WarehouseValidationException(
          "Warehouse with business unit code '" + warehouse.businessUnitCode + "' already exists.");
    }

    Location location = locationResolver.resolveByIdentifier(warehouse.location);
    validateLocation(location, warehouse);

    long warehousesAtLocation = warehouseStore.getAll().stream()
        .filter(this::isActive)
        .filter(w -> warehouse.location.equals(w.location))
        .count();

    if (warehousesAtLocation >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
          "Maximum number of warehouses for location '" + warehouse.location + "' has been reached.");
    }

    int currentCapacity = warehouseStore.getAll().stream()
        .filter(this::isActive)
        .filter(w -> warehouse.location.equals(w.location))
        .map(w -> w.capacity == null ? 0 : w.capacity)
        .reduce(0, Integer::sum);

    if (currentCapacity + warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Maximum capacity for location '" + warehouse.location + "' would be exceeded.");
    }

    if (warehouse.createdAt == null) {
      warehouse.createdAt = java.time.LocalDateTime.now();
    }
    warehouse.archivedAt = null;
    warehouseStore.create(warehouse);
  }

  private void validateWarehouse(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WarehouseValidationException("Warehouse request must not be null.");
    }
    if (warehouse.businessUnitCode == null || warehouse.businessUnitCode.isBlank()) {
      throw new WarehouseValidationException("Business unit code is required.");
    }
    if (warehouse.location == null || warehouse.location.isBlank()) {
      throw new WarehouseValidationException("Location is required.");
    }
    if (warehouse.capacity == null || warehouse.capacity < 0) {
      throw new WarehouseValidationException("Capacity must be a non-negative value.");
    }
    if (warehouse.stock == null || warehouse.stock < 0) {
      throw new WarehouseValidationException("Stock must be a non-negative value.");
    }
    if (warehouse.stock > warehouse.capacity) {
      throw new WarehouseValidationException("Warehouse capacity must accommodate the informed stock.");
    }
  }

  private void validateLocation(Location location, Warehouse warehouse) {
    if (location == null) {
      throw new WarehouseValidationException("Location '" + warehouse.location + "' does not exist.");
    }
    if (warehouse.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Warehouse capacity cannot exceed the maximum capacity of location '" + warehouse.location + "'.");
    }
  }

  private boolean isActive(Warehouse warehouse) {
    return warehouse != null && warehouse.archivedAt == null;
  }
}

