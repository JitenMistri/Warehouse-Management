package com.fulfilment.application.monolith.stores.events;

import com.fulfilment.application.monolith.stores.LegacyStoreManagerGateway;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class StoreEventObserver {

    private final LegacyStoreManagerGateway legacyStoreManagerGateway;

    public StoreEventObserver(LegacyStoreManagerGateway legacyStoreManagerGateway) {
        this.legacyStoreManagerGateway = legacyStoreManagerGateway;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStoreCreated(StoreCreatedEvent event) {
        legacyStoreManagerGateway.createStoreOnLegacySystem(event.store());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleStoreUpdated(StoreUpdatedEvent event) {
        legacyStoreManagerGateway.updateStoreOnLegacySystem(event.store());
    }
}