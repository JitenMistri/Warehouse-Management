package com.fulfilment.application.monolith.fulfillment.domain.validator;

import com.fulfilment.application.monolith.fulfillment.domain.port.FulfillmentAssignmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import org.springframework.stereotype.Component;

@Component
public class FulfillmentValidator {

    private final FulfillmentAssignmentStore assignmentStore;

    public FulfillmentValidator(
            FulfillmentAssignmentStore assignmentStore) {
        this.assignmentStore = assignmentStore;
    }

    public void validate(
            Store store,
            Product product,
            DbWarehouse warehouse) {

        boolean alreadyAssigned =
                assignmentStore.countByStoreAndProductAndWarehouse(
                        store,
                        product,
                        warehouse) > 0;

        if (alreadyAssigned) {
            throw new FulfillmentValidationException(
                    "This product is already assigned to this warehouse and store.");
        }

        long existingSameProductAndStore =
                assignmentStore.countByStoreAndProduct(store, product);

        if (existingSameProductAndStore >= 2) {
            throw new FulfillmentValidationException(
                    "A product can be fulfilled by at most 2 warehouses per store.");
        }

        boolean alreadyAssignedToStore =
                assignmentStore.countByStoreAndWarehouse(
                        store,
                        warehouse) > 0;

        long warehousesForStore =
                assignmentStore.countDistinctWarehousesByStore(store);

        if (!alreadyAssignedToStore && warehousesForStore >= 3) {
            throw new FulfillmentValidationException(
                    "A store can be fulfilled by at most 3 warehouses.");
        }

        boolean alreadyAssignedProduct =
                assignmentStore.countByWarehouseAndProduct(
                        warehouse,
                        product) > 0;

        long productsForWarehouse =
                assignmentStore.countDistinctProductsByWarehouse(
                        warehouse);

        if (!alreadyAssignedProduct && productsForWarehouse >= 5) {
            throw new FulfillmentValidationException(
                    "A warehouse can store at most 5 product types.");
        }
    }
}