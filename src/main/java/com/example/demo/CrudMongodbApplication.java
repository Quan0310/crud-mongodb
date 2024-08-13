package com.example.demo;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.demo.models.Product;
import com.example.demo.service.ProductService;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class CrudMongodbApplication {

	public static void main(String[] args) {
		SpringApplication.run(CrudMongodbApplication.class, args);
	}

	
	@Autowired
	private ProductService productService;
	
	@PostConstruct
    public void afterStartup() throws IOException {
        System.out.println("Bắt đầu đồng bộ...");
        productService.deleteAllProductsFromElasticsearch();
        Iterable<Product> products = productService.getProductsToSync();
        if (products == null || !products.iterator().hasNext()) {
            System.out.println("Không có sản phẩm nào để đồng bộ.");
        } else {
            productService.addProductsBulk(products);
        }
    }
}
