package com.fulfilment.application.monolith;

import com.fulfilment.application.monolith.fulfillment.FulfillmentResource;
import com.fulfilment.application.monolith.products.ProductResource;
import com.fulfilment.application.monolith.stores.StoreResource;
import com.fulfilment.application.monolith.warehouses.adapters.restapi.WarehouseResourceImpl.WarehouseNotFoundApiException;
import com.fulfilment.application.monolith.warehouses.domain.usecases.WarehouseNotFoundException;
import com.fulfilment.application.monolith.warehouses.domain.usecases.WarehouseValidationException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

  @ExceptionHandler({ProductResource.ApiException.class, StoreResource.ApiException.class,
      FulfillmentResource.ApiException.class})
  public ResponseEntity<Map<String, Object>> handleKnownApiException(RuntimeException exception) {
    int status = 500;
    if (exception instanceof ProductResource.ApiException e) status = e.getStatus();
    if (exception instanceof StoreResource.ApiException e) status = e.getStatus();
    if (exception instanceof FulfillmentResource.ApiException e) status = e.getStatus();
    return body(exception, status);
  }

  @ExceptionHandler({WarehouseValidationException.class})
  public ResponseEntity<Map<String, Object>> handleWarehouseValidation(WarehouseValidationException exception) {
    return body(exception, 400);
  }

  @ExceptionHandler({WarehouseNotFoundException.class, WarehouseNotFoundApiException.class})
  public ResponseEntity<Map<String, Object>> handleWarehouseNotFound(RuntimeException exception) {
    return body(exception, 404);
  }

  private ResponseEntity<Map<String, Object>> body(Exception exception, int status) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("exceptionType", exception.getClass().getName());
    body.put("code", status);
    body.put("error", exception.getMessage());
    return ResponseEntity.status(status).body(body);
  }
}
