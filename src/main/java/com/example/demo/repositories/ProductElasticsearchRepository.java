package com.example.demo.repositories;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.example.demo.models.ProductElastic;

public interface ProductElasticsearchRepository extends ElasticsearchRepository<ProductElastic, String> {
}