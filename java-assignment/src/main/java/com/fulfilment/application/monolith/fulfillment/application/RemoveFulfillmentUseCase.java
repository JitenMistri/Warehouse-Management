package com.fulfilment.application.monolith.fulfillment.application;

import com.fulfilment.application.monolith.fulfillment.domain.model.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.domain.port.FulfillmentAssignmentStore;
import com.fulfilment.application.monolith.fulfillment.domain.validator.FulfillmentNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class RemoveFulfillmentUseCase {

    private final FulfillmentAssignmentStore assignmentStore;

    public RemoveFulfillmentUseCase(
            FulfillmentAssignmentStore assignmentStore) {
        this.assignmentStore = assignmentStore;
    }

    public void remove(Long id) {

        FulfillmentAssignment assignment = assignmentStore.find(id);

        if (assignment == null) {
            throw new FulfillmentNotFoundException(
                    "Fulfillment assignment with id " + id + " does not exist.");
        }

        assignmentStore.delete(assignment);
    }
}