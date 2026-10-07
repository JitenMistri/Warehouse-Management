package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore, LocationResolver locationResolver) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    if (newWarehouse == null || newWarehouse.businessUnitCode == null
        || newWarehouse.businessUnitCode.isBlank()) {
      throw new WarehouseValidationException("Business unit code is required.");
    }

    Warehouse current = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (current == null) {
      throw new WarehouseNotFoundException(
          "Warehouse with business unit code '" + newWarehouse.businessUnitCode + "' does not exist.");
    }

    validate(newWarehouse, current);

    // A replacement is intentionally performed as archive + create so the old
    // warehouse remains available in the database as historical data.
    current.archivedAt = LocalDateTime.now();
    warehouseStore.update(current);

    newWarehouse.createdAt = LocalDateTime.now();
    newWarehouse.archivedAt = null;
    warehouseStore.create(newWarehouse);
  }

  private void validate(Warehouse replacement, Warehouse current) {
    if (replacement.location == null || replacement.location.isBlank()) {
      throw new WarehouseValidationException("Location is required.");
    }
    if (replacement.capacity == null || replacement.capacity < 0) {
      throw new WarehouseValidationException("Capacity must be a non-negative value.");
    }
    if (replacement.stock == null || replacement.stock < 0) {
      throw new WarehouseValidationException("Stock must be a non-negative value.");
    }

    if (!Integer.valueOf(current.stock == null ? 0 : current.stock).equals(replacement.stock)) {
      throw new WarehouseValidationException("Replacement stock must match the current warehouse stock.");
    }
    if (replacement.capacity < (current.stock == null ? 0 : current.stock)) {
      throw new WarehouseValidationException("Replacement capacity must accommodate the current stock.");
    }

    Location location = locationResolver.resolveByIdentifier(replacement.location);
    if (location == null) {
      throw new WarehouseValidationException("Location '" + replacement.location + "' does not exist.");
    }
    if (replacement.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Warehouse capacity cannot exceed the maximum capacity of location '" + replacement.location + "'.");
    }

    var activeWarehouses = warehouseStore.getAll().stream()
        .filter(w -> w.archivedAt == null)
        .filter(w -> !replacement.businessUnitCode.equals(w.businessUnitCode))
        .toList();

    long countAtLocation = activeWarehouses.stream()
        .filter(w -> replacement.location.equals(w.location))
        .count();
    if (countAtLocation >= location.maxNumberOfWarehouses) {
      throw new WarehouseValidationException(
          "Maximum number of warehouses for location '" + replacement.location + "' has been reached.");
    }

    int capacityAtLocation = activeWarehouses.stream()
        .filter(w -> replacement.location.equals(w.location))
        .map(w -> w.capacity == null ? 0 : w.capacity)
        .reduce(0, Integer::sum);
    if (capacityAtLocation + replacement.capacity > location.maxCapacity) {
      throw new WarehouseValidationException(
          "Maximum capacity for location '" + replacement.location + "' would be exceeded.");
    }
  }
}
