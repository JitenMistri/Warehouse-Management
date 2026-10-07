package com.fulfilment.application.monolith.fulfillment.application;

import com.fulfilment.application.monolith.fulfillment.domain.model.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.domain.port.FulfillmentAssignmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validator.FulfillmentValidator;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import org.springframework.stereotype.Service;

@Service
public class CreateFulfillmentUseCase {

    private final FulfillmentAssignmentStore assignmentStore;
    private final FulfillmentValidator validator;

    public CreateFulfillmentUseCase(
            FulfillmentAssignmentStore assignmentStore,
            FulfillmentValidator validator) {
        this.assignmentStore = assignmentStore;
        this.validator = validator;
    }

    public FulfillmentAssignment create(
            Store store,
            Product product,
            DbWarehouse warehouse) {

        validator.validate(store, product, warehouse);

        FulfillmentAssignment assignment = new FulfillmentAssignment();
        assignment.store = store;
        assignment.product = product;
        assignment.warehouse = warehouse;

        return assignmentStore.create(assignment);
    }
}