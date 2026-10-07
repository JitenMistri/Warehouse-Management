package com.fulfilment.application.monolith.fulfillment.domain.validator;

public class FulfillmentNotFoundException extends RuntimeException {

    public FulfillmentNotFoundException(String message) {
        super(message);
    }
}