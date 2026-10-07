package com.fulfilment.application.monolith.warehouses.domain.usecases;

public class WarehouseValidationException extends RuntimeException {

  public WarehouseValidationException(String message) {
    super(message);
  }
}
