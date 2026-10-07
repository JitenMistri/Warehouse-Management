package com.fulfilment.application.monolith.stores;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

class LegacyStoreManagerGatewayTest {

  @Test
  void shouldSynchronizeStoreWithoutThrowing() {
    Store store = new Store("LEGACY-TEST");
    store.quantityProductsInStock = 10;
    LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

    assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
  }
}
