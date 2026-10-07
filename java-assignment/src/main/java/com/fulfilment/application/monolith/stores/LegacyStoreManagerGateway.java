package com.fulfilment.application.monolith.stores;

import java.nio.file.Files;
import java.nio.file.Path;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LegacyStoreManagerGateway {

  public void createStoreOnLegacySystem(Store store) { writeToFile(store); }

  public void updateStoreOnLegacySystem(Store store) { writeToFile(store); }

  private void writeToFile(Store store) {
    try {
      Path tempFile = Files.createTempFile(store.name, ".txt");
      String content = "Store created. [ name =" + store.name + " ] [ items on stock =" + store.quantityProductsInStock + "]";
      Files.write(tempFile, content.getBytes());
      Files.readAllBytes(tempFile);
      Files.delete(tempFile);
    } catch (Exception e) {
      log.error("writeToFile(): Exception = {}", e.getMessage(), e);
    }
  }
}
