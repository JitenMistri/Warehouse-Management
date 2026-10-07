package com.fulfilment.application.monolith.fulfillment;

import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/fulfillment")
public class FulfillmentResource {

  private final FulfillmentAssignmentRepository assignmentRepository;
  private final WarehouseRepository warehouseRepository;
  private final StoreRepository storeRepository;
  private final ProductRepository productRepository;

  public FulfillmentResource(FulfillmentAssignmentRepository assignmentRepository,
      WarehouseRepository warehouseRepository, StoreRepository storeRepository,
      ProductRepository productRepository) {
    this.assignmentRepository = assignmentRepository;
    this.warehouseRepository = warehouseRepository;
    this.storeRepository = storeRepository;
    this.productRepository = productRepository;
  }

  @PostMapping
  @Transactional
  public ResponseEntity<AssignmentResponse> assign(@RequestBody FulfillmentRequest request) {
    if (request == null || request.storeId == null || request.productId == null
        || request.warehouseBusinessUnitCode == null || request.warehouseBusinessUnitCode.isBlank()) {
      throw new ApiException("storeId, productId and warehouseBusinessUnitCode are required.", 400);
    }

    Store store = storeRepository.findById(request.storeId)
        .orElseThrow(() -> new ApiException("Store with id " + request.storeId + " does not exist.", 404));
    Product product = productRepository.findById(request.productId)
        .orElseThrow(() -> new ApiException("Product with id " + request.productId + " does not exist.", 404));
    DbWarehouse warehouse = warehouseRepository.findActiveEntity(request.warehouseBusinessUnitCode)
        .orElseThrow(() -> new ApiException("Active warehouse '" + request.warehouseBusinessUnitCode + "' does not exist.", 404));

    long existingSameProductAndStore = assignmentRepository.countByStoreAndProduct(store, product);
    boolean alreadyAssignedToStore = assignmentRepository.countByStoreAndWarehouse(store, warehouse) > 0;
    boolean alreadyAssignedProduct = assignmentRepository.countByWarehouseAndProduct(warehouse, product) > 0;
    boolean alreadyAssigned = assignmentRepository.countByStoreAndProductAndWarehouse(store, product, warehouse) > 0;

    if (alreadyAssigned) {
      throw new ApiException("This product is already assigned to this warehouse and store.", 400);
    }
    if (existingSameProductAndStore >= 2) {
      throw new ApiException("A product can be fulfilled by at most 2 warehouses per store.", 400);
    }

    long warehousesForStore =
            assignmentRepository.countDistinctWarehousesByStore(store);

    if (!alreadyAssignedToStore && warehousesForStore >= 3) {
      throw new ApiException("A store can be fulfilled by at most 3 warehouses.", 400);
    }

    long productsForWarehouse =
            assignmentRepository.countDistinctProductsByWarehouse(warehouse);

    if (!alreadyAssignedProduct && productsForWarehouse >= 5) {
      throw new ApiException("A warehouse can store at most 5 product types.", 400);
    }

    FulfillmentAssignment assignment = new FulfillmentAssignment();
    assignment.store = store;
    assignment.product = product;
    assignment.warehouse = warehouse;
    assignmentRepository.save(assignment);

    return ResponseEntity.status(201).body(new AssignmentResponse(store.id, product.id, warehouse.businessUnitCode));
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> remove(@PathVariable Long id) {
    FulfillmentAssignment assignment = assignmentRepository.findById(id)
        .orElseThrow(() -> new ApiException("Fulfillment assignment with id " + id + " does not exist.", 404));
    assignmentRepository.delete(assignment);
    return ResponseEntity.noContent().build();
  }

  public static class FulfillmentRequest {
    public Long storeId;
    public Long productId;
    public String warehouseBusinessUnitCode;
  }

  public record AssignmentResponse(Long storeId, Long productId, String warehouseBusinessUnitCode) {}

  public static class ApiException extends RuntimeException {
    private final int status;
    public ApiException(String message, int status) { super(message); this.status = status; }
    public int getStatus() { return status; }
  }
}
