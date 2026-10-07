package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.ArrayList;
import java.util.List;

final class WarehouseUseCaseTestSupport {

  private WarehouseUseCaseTestSupport() {}

  static LocationResolver locations() {
    return identifier -> switch (identifier) {
      case "AMSTERDAM-002" -> new Location("AMSTERDAM-002", 3, 75);
      case "ZWOLLE-001" -> new Location("ZWOLLE-001", 1, 40);
      default -> null;
    };
  }

  static Warehouse warehouse(String code, String location, int capacity, int stock) {
    Warehouse warehouse = new Warehouse();
    warehouse.businessUnitCode = code;
    warehouse.location = location;
    warehouse.capacity = capacity;
    warehouse.stock = stock;
    return warehouse;
  }

  static class InMemoryWarehouseStore implements WarehouseStore {
    final List<Warehouse> warehouses = new ArrayList<>();

    @Override
    public List<Warehouse> getAll() {
      return warehouses;
    }

    @Override
    public void create(Warehouse warehouse) {
      warehouses.add(warehouse);
    }

    @Override
    public void update(Warehouse warehouse) {
      for (int i = 0; i < warehouses.size(); i++) {
        if (warehouses.get(i).businessUnitCode.equals(warehouse.businessUnitCode)
            && warehouses.get(i).archivedAt == null) {
          warehouses.set(i, warehouse);
          return;
        }
      }
      warehouses.add(warehouse);
    }

    @Override
    public void remove(Warehouse warehouse) {
      warehouses.remove(warehouse);
    }

    @Override
    public Warehouse findByBusinessUnitCode(String buCode) {
      return warehouses.stream()
          .filter(w -> buCode.equals(w.businessUnitCode) && w.archivedAt == null)
          .findFirst()
          .orElse(null);
    }
  }
}
