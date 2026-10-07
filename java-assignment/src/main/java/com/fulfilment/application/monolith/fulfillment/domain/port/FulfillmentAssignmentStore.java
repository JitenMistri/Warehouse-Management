package com.fulfilment.application.monolith.fulfillment.domain.port;

import com.fulfilment.application.monolith.fulfillment.domain.model.FulfillmentAssignment;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;

public interface FulfillmentAssignmentStore {

    long countByStoreAndProduct(Store store, Product product);

    long countByStoreAndWarehouse(Store store, DbWarehouse warehouse);

    long countByWarehouseAndProduct(DbWarehouse warehouse, Product product);

    long countByStoreAndProductAndWarehouse(
            Store store,
            Product product,
            DbWarehouse warehouse);

    long countDistinctProductsByWarehouse(DbWarehouse warehouse);

    long countDistinctWarehousesByStore(Store store);

    FulfillmentAssignment create(FulfillmentAssignment assignment);

    FulfillmentAssignment find(Long id);

    void delete(FulfillmentAssignment assignment);
}