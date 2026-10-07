package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouse")
public class WarehouseResourceImpl {

  private final WarehouseRepository warehouseRepository;
  private final CreateWarehouseOperation createWarehouseOperation;
  private final ReplaceWarehouseOperation replaceWarehouseOperation;
  private final ArchiveWarehouseOperation archiveWarehouseOperation;

  public WarehouseResourceImpl(WarehouseRepository warehouseRepository,
      CreateWarehouseOperation createWarehouseOperation,
      ReplaceWarehouseOperation replaceWarehouseOperation,
      ArchiveWarehouseOperation archiveWarehouseOperation) {
    this.warehouseRepository = warehouseRepository;
    this.createWarehouseOperation = createWarehouseOperation;
    this.replaceWarehouseOperation = replaceWarehouseOperation;
    this.archiveWarehouseOperation = archiveWarehouseOperation;
  }

  @GetMapping
  public List<WarehouseResponse> listAllWarehousesUnits() {
    return warehouseRepository.getAll().stream()
        .filter(warehouse -> warehouse.archivedAt == null)
        .map(this::toResponse)
        .toList();
  }

  @PostMapping
  @Transactional
  public WarehouseResponse createANewWarehouseUnit(@RequestBody Warehouse data) {
    createWarehouseOperation.create(data);
    return toResponse(data);
  }

  @GetMapping("/{id}")
  public WarehouseResponse getAWarehouseUnitByID(@PathVariable String id) {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) {
      throw new WarehouseNotFoundApiException("Warehouse with business unit code '" + id + "' does not exist.");
    }
    return toResponse(warehouse);
  }

  @DeleteMapping("/{id}")
  @Transactional
  public void archiveAWarehouseUnitByID(@PathVariable String id) {
    Warehouse warehouse = warehouseRepository.findByBusinessUnitCode(id);
    if (warehouse == null) {
      throw new WarehouseNotFoundApiException("Warehouse with business unit code '" + id + "' does not exist.");
    }
    archiveWarehouseOperation.archive(warehouse);
  }

  @PostMapping("/{businessUnitCode}/replacement")
  @Transactional
  public WarehouseResponse replaceTheCurrentActiveWarehouse(@PathVariable String businessUnitCode,
      @RequestBody Warehouse data) {
    data.businessUnitCode = businessUnitCode;
    replaceWarehouseOperation.replace(data);
    return toResponse(data);
  }

  private WarehouseResponse toResponse(Warehouse warehouse) {
    return new WarehouseResponse(warehouse.businessUnitCode, warehouse.location, warehouse.capacity, warehouse.stock);
  }

  public record WarehouseResponse(String businessUnitCode, String location, Integer capacity, Integer stock) {}

  public static class WarehouseNotFoundApiException extends RuntimeException {
    public WarehouseNotFoundApiException(String message) { super(message); }
  }
}
