package com.fulfilment.application.monolith.stores;

import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Cacheable
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  public Long id;

  @Column(length = 40, unique = true)
  public String name;

  public int quantityProductsInStock;

  public Store() {}

  public Store(String name) {
    this.name = name;
  }
}
