package com.fulfilment.application.monolith.products;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/product")
public class ProductResource {

  private final ProductRepository productRepository;

  public ProductResource(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @GetMapping
  public List<Product> get() {
    return productRepository.findAll(org.springframework.data.domain.Sort.by("name"));
  }

  @GetMapping("/{id}")
  public Product getSingle(@PathVariable Long id) {
    return productRepository.findById(id)
        .orElseThrow(() -> new ApiException("Product with id of " + id + " does not exist.", 404));
  }

  @PostMapping
  @Transactional
  public ResponseEntity<Product> create(@RequestBody Product product) {
    if (product.id != null) {
      throw new ApiException("Id was invalidly set on request.", 422);
    }
    productRepository.save(product);
    return ResponseEntity.status(201).body(product);
  }

  @PutMapping("/{id}")
  @Transactional
  public Product update(@PathVariable Long id, @RequestBody Product product) {
    if (product.name == null) {
      throw new ApiException("Product Name was not set on request.", 422);
    }
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> new ApiException("Product with id of " + id + " does not exist.", 404));
    entity.name = product.name;
    entity.description = product.description;
    entity.price = product.price;
    entity.stock = product.stock;
    return productRepository.save(entity);
  }

  @DeleteMapping("/{id}")
  @Transactional
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    Product entity = productRepository.findById(id)
        .orElseThrow(() -> new ApiException("Product with id of " + id + " does not exist.", 404));
    productRepository.delete(entity);
    return ResponseEntity.noContent().build();
  }

  public static class ApiException extends RuntimeException {
    private final int status;
    public ApiException(String message, int status) { super(message); this.status = status; }
    public int getStatus() { return status; }
  }
}
