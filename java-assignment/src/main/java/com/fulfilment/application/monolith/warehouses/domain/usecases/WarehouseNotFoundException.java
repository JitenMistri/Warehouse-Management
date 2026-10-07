package com.fulfilment.application.monolith.warehouses.domain.usecases;

public class WarehouseNotFoundException extends RuntimeException {

  public WarehouseNotFoundException(String message) {
    super(message);
  }
}
