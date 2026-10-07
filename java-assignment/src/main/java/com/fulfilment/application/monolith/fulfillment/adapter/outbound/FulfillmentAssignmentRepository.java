package com.fulfilment.application.monolith.fulfillment.adapter.outbound;

import com.fulfilment.application.monolith.fulfillment.domain.model.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.domain.port.FulfillmentAssignmentStore;
import com.fulfilment.application.monolith.products.Product;
import com.fulfilment.application.monolith.stores.Store;
import com.fulfilment.application.monolith.warehouses.adapters.database.DbWarehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FulfillmentAssignmentRepository
        extends JpaRepository<FulfillmentAssignment, Long>,
        FulfillmentAssignmentStore {

    long countByStoreAndProduct(Store store, Product product);

    long countByStoreAndWarehouse(Store store, DbWarehouse warehouse);

    long countByWarehouseAndProduct(
            DbWarehouse warehouse,
            Product product);

    long countByStoreAndProductAndWarehouse(
            Store store,
            Product product,
            DbWarehouse warehouse);

    @Query("""
        SELECT COUNT(DISTINCT a.product.id)
        FROM FulfillmentAssignment a
        WHERE a.warehouse = :warehouse
        """)
    long countDistinctProductsByWarehouse(
            @Param("warehouse") DbWarehouse warehouse);

    @Query("""
        SELECT COUNT(DISTINCT a.warehouse.id)
        FROM FulfillmentAssignment a
        WHERE a.store = :store
        """)
    long countDistinctWarehousesByStore(
            @Param("store") Store store);

    @Override
    default FulfillmentAssignment create(
            FulfillmentAssignment assignment) {
        return save(assignment);
    }

    @Override
    default FulfillmentAssignment find(Long id) {
        return findById(id).orElse(null);
    }
}