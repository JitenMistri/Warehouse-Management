package com.fulfilment.application.monolith.location;

import static org.junit.jupiter.api.Assertions.*;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import org.junit.jupiter.api.Test;

class LocationGatewayTest {

  private final LocationGateway locationGateway = new LocationGateway();

  @Test
  void shouldResolveExistingLocation() {
    Location location = locationGateway.resolveByIdentifier("ZWOLLE-001");

    assertNotNull(location);
    assertEquals("ZWOLLE-001", location.identification);
    assertEquals(1, location.maxNumberOfWarehouses);
    assertEquals(40, location.maxCapacity);
  }

  @Test
  void shouldReturnNullForUnknownLocation() {
    assertNull(locationGateway.resolveByIdentifier("UNKNOWN"));
  }

  @Test
  void shouldReturnNullForNullIdentifier() {
    assertNull(locationGateway.resolveByIdentifier(null));
  }
}
