package com.example.demo.repositories;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.models.Product;

public interface ProductRepository extends MongoRepository<Product, String> {
    List<Product> findByName(String name);
}