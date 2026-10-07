package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    if (warehouse == null) {
      throw new WarehouseNotFoundException("Warehouse does not exist.");
    }
    if (warehouse.archivedAt != null) {
      return;
    }
    warehouse.archivedAt = LocalDateTime.now();
    warehouseStore.update(warehouse);
  }
}
