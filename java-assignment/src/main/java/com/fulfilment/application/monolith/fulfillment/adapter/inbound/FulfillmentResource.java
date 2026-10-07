package com.fulfilment.application.monolith.fulfillment.adapter.inbound;

import com.fulfilment.application.monolith.fulfillment.application.CreateFulfillmentUseCase;
import com.fulfilment.application.monolith.fulfillment.application.RemoveFulfillmentUseCase;
import com.fulfilment.application.monolith.fulfillment.domain.model.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.domain.validator.FulfillmentNotFoundException;
import com.fulfilment.application.monolith.fulfillment.domain.validator.FulfillmentValidationException;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.products.ProductRepository;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.stores.StoreRepository;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fulfillment")
public class FulfillmentResource {

    private final CreateFulfillmentUseCase createFulfillmentUseCase;
    private final RemoveFulfillmentUseCase removeFulfillmentUseCase;
    private final WarehouseRepository warehouseRepository;
    private final StoreRepository storeRepository;
    private final ProductRepository productRepository;

    public FulfillmentResource(
            CreateFulfillmentUseCase createFulfillmentUseCase,
            RemoveFulfillmentUseCase removeFulfillmentUseCase,
            WarehouseRepository warehouseRepository,
            StoreRepository storeRepository,
            ProductRepository productRepository) {

        this.createFulfillmentUseCase = createFulfillmentUseCase;
        this.removeFulfillmentUseCase = removeFulfillmentUseCase;
        this.warehouseRepository = warehouseRepository;
        this.storeRepository = storeRepository;
        this.productRepository = productRepository;
    }

    @PostMapping
    @Transactional
    public ResponseEntity<AssignmentResponse> assign(
            @RequestBody FulfillmentRequest request) {

        if (request == null ||
                request.storeId == null ||
                request.productId == null ||
                request.warehouseBusinessUnitCode == null ||
                request.warehouseBusinessUnitCode.isBlank()) {

            throw new ApiException(
                    "storeId, productId and warehouseBusinessUnitCode are required.",
                    400);
        }

        Store store = storeRepository.findById(request.storeId)
                .orElseThrow(() -> new ApiException(
                        "Store with id " + request.storeId + " does not exist.",
                        404));

        Product product = productRepository.findById(request.productId)
                .orElseThrow(() -> new ApiException(
                        "Product with id " + request.productId + " does not exist.",
                        404));

        DbWarehouse warehouse =
                warehouseRepository
                        .findActiveEntity(request.warehouseBusinessUnitCode)
                        .orElseThrow(() -> new ApiException(
                                "Active warehouse '" +
                                request.warehouseBusinessUnitCode +
                                "' does not exist.",
                                404));

        try {
            FulfillmentAssignment assignment =
                    createFulfillmentUseCase.create(
                            store,
                            product,
                            warehouse);

            return ResponseEntity
                    .status(201)
                    .body(new AssignmentResponse(
                            assignment.store.id,
                            assignment.product.id,
                            assignment.warehouse.businessUnitCode));

        } catch (FulfillmentValidationException e) {
            throw new ApiException(e.getMessage(), 400);
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> remove(@PathVariable Long id) {

        try {
            removeFulfillmentUseCase.remove(id);
            return ResponseEntity.noContent().build();

        } catch (FulfillmentNotFoundException e) {
            throw new ApiException(e.getMessage(), 404);
        }
    }

    public static class FulfillmentRequest {
        public Long storeId;
        public Long productId;
        public String warehouseBusinessUnitCode;
    }

    public record AssignmentResponse(
            Long storeId,
            Long productId,
            String warehouseBusinessUnitCode) {
    }

    public static class ApiException extends RuntimeException {

        private final int status;

        public ApiException(String message, int status) {
            super(message);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }
    }
}