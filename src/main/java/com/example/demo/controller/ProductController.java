package com.example.demo.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.Product;
import com.example.demo.service.ProductService;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/products")
public class ProductController {

	@Autowired
	private ProductService productService;

	@Operation(method = "POST", summary = "Add product", description = "Send a request via this API to add product")
	@PostMapping
	public ResponseEntity<String> saveProduct(@RequestBody Product product) {
		productService.saveProduct(product);
		return ResponseEntity.ok().body("Product added!");
	}

	@Operation(method = "GET", summary = "Find all products", description = "Send a request via this API to find all products")
	@GetMapping
	public ResponseEntity<List<Product>> getAll() {
		List<Product> products = productService.findAll();
		return ResponseEntity.ok(products);
	}

	@Operation(method = "GET", summary = "Find product by Id", description = "Send a request via this API to find product by Id")
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable String id) {
		return productService.getProductById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

	@Operation(method = "PUT", summary = "Edit product", description = "Send a request via this API to edit product")
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable String id, @RequestBody Product product) {
		Product updatedProduct = productService.updateProduct(id, product);
		if (updatedProduct != null) {
			return ResponseEntity.ok(updatedProduct);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@Operation(method = "DELETE", summary = "Delete product", description = "Send a request via this API to delete product by Id")
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
		productService.deleteProduct(id);
		return ResponseEntity.noContent().build();
	}

	@Operation(method = "GET", summary = "Search product", description = "Send a request via this API to search products")
	@GetMapping("/matchAllProducts/{fieldValue}")
	public List<Product> matchAllProductsWithName(@PathVariable String fieldValue) throws IOException {
		SearchResponse<Product> searchResponse = productService.matchProductsWithName(fieldValue);
		System.out.println(searchResponse.hits().hits().toString());

		List<Hit<Product>> listOfHits = searchResponse.hits().hits();
		List<Product> listOfProducts = new ArrayList<>();
		for (Hit<Product> hit : listOfHits) {
			listOfProducts.add(hit.source());
		}
		return listOfProducts;
	}

}