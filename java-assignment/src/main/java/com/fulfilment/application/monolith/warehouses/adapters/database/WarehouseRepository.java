package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WarehouseRepository extends JpaRepository<DbWarehouse, Long>, WarehouseStore {

  @Override
  default List<Warehouse> getAll() {
    return findAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  default void create(Warehouse warehouse) {
    DbWarehouse entity = new DbWarehouse();
    beanToEntity(warehouse, entity);
    save(entity);
  }

  @Override
  default void update(Warehouse warehouse) {
    DbWarehouse entity = findActiveEntity(warehouse.businessUnitCode)
        .orElseGet(() -> findLatestByBusinessUnitCode(warehouse.businessUnitCode)
            .orElseThrow(() -> new IllegalArgumentException("Warehouse with business unit code '"
                + warehouse.businessUnitCode + "' does not exist.")));
    beanToEntity(warehouse, entity);
    save(entity);
  }

  @Override
  default void remove(Warehouse warehouse) {
    findActiveEntity(warehouse.businessUnitCode).ifPresent(this::delete);
  }

  @Override
  default Warehouse findByBusinessUnitCode(String buCode) {
    return findActiveEntity(buCode).map(DbWarehouse::toWarehouse).orElse(null);
  }

  default Optional<DbWarehouse> findActiveEntity(String buCode) {
    return findFirstByBusinessUnitCodeAndArchivedAtIsNullOrderByIdAsc(buCode);
  }

  Optional<DbWarehouse> findFirstByBusinessUnitCodeAndArchivedAtIsNullOrderByIdAsc(String buCode);

  Optional<DbWarehouse> findFirstByBusinessUnitCodeOrderByIdDesc(String buCode);

  default Optional<DbWarehouse> findLatestByBusinessUnitCode(String buCode) {
    return findFirstByBusinessUnitCodeOrderByIdDesc(buCode);
  }

  List<DbWarehouse> findAllByArchivedAtIsNull();

  private static void beanToEntity(Warehouse source, DbWarehouse target) {
    target.businessUnitCode = source.businessUnitCode;
    target.location = source.location;
    target.capacity = source.capacity;
    target.stock = source.stock;
    target.createdAt = source.createdAt;
    target.archivedAt = source.archivedAt;
  }
}
