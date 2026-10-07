package com.fulfilment.application.monolith.fulfillment.domain.validator;

public class FulfillmentValidationException extends RuntimeException {

    public FulfillmentValidationException(String message) {
        super(message);
    }
}