package com.example.demo.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.demo.models.Product;
import com.example.demo.models.ProductElastic;
import com.example.demo.repositories.ProductElasticsearchRepository;
import com.example.demo.repositories.ProductRepository;
import com.example.demo.utils.ElasticsearchUtil;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;

@Service
public class ProductService {
	 private static final Logger LOG = LoggerFactory.getLogger(ProductService.class);
	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductElasticsearchRepository productElasticsearchRepository;

	@Autowired
	private ElasticsearchClient elasticsearchClient;
	public Product saveProduct(Product product) {
		Product savedProduct = productRepository.save(product);

		ProductElastic productElastic = new ProductElastic();
		productElastic.setId(savedProduct.getId());
		productElastic.setName(savedProduct.getName());
		productElastic.setDescription(savedProduct.getDescription());
		productElastic.setPrice(savedProduct.getPrice());

		productElasticsearchRepository.save(productElastic);

		return savedProduct;
	}

	public List<Product> findAll() {
		return productRepository.findAll();
	}

	@Cacheable(value = "product", key = "#id")
	public Optional<Product> getProductById(String id) {
		return productRepository.findById(id);
	}

	@CacheEvict(value = "product", key = "#id")
	public Product updateProduct(String id, Product product) {
		Optional<Product> existingProduct = productRepository.findById(id);
		if (existingProduct.isPresent()) {
			product.setId(id);
			Product updatedProduct = productRepository.save(product);

			ProductElastic productElastic = new ProductElastic();
			productElastic.setId(updatedProduct.getId());
			productElastic.setName(updatedProduct.getName());
			productElastic.setDescription(updatedProduct.getDescription());
			productElastic.setPrice(updatedProduct.getPrice());

			productElasticsearchRepository.save(productElastic);

			return updatedProduct;
		}
		return null;
	}

	@CacheEvict(value = "product", key = "#id")
	public void deleteProduct(String id) {
		productRepository.deleteById(id);
		productElasticsearchRepository.deleteById(id);
	}
	
	public Iterable<Product> getProductsToSync() {
		return productRepository.findAll();
	}
	
	public void deleteAllProductsFromElasticsearch() {
		try {
			DeleteByQueryRequest deleteByQueryRequest = DeleteByQueryRequest
					.of(b -> b.index("products").query(q -> q.matchAll(m -> m)));

			DeleteByQueryResponse response = elasticsearchClient.deleteByQuery(deleteByQueryRequest);
		
			System.out.println("deleteByQueryRequest: " + deleteByQueryRequest);
			System.out.println("Deleted documents: " + response.deleted());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	 public void addProductsBulk(Iterable<Product> products) throws IOException {
	        BulkRequest.Builder bulkRequest = new BulkRequest.Builder();

	        for (Product product : products) {
	        	bulkRequest.operations(op -> op           
	                    .index(idx -> idx            
	                        .index("products")       
	                        .id(product.getId().toString())
	                        .document(product)
	                    )
	                );
	        }
	        BulkResponse bulkResponse = elasticsearchClient.bulk(bulkRequest.build());

	        if (bulkResponse.errors()) {
	        	System.out.println("Bulk had errors");
	            for (BulkResponseItem item: bulkResponse.items()) {
	                if (item.error() != null) {
	                	System.out.println(item.error().reason());
	                }
	            }
	        }
	    }

		public SearchResponse<Product> matchProductsWithName(String fieldValue) throws IOException {
			Supplier<Query> supplier = ElasticsearchUtil.supplierWithNameField3rd(fieldValue);
			SearchResponse<Product> searchResponse = elasticsearchClient
					.search(s -> s.index("products").query(supplier.get()), Product.class);
			LOG.info("elasticsearchs query is " + supplier.get().toString());
			return searchResponse;
		}

	 
}