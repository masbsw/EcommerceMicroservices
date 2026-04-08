package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.StockUpdateRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.ResourceNotFoundException;
import com.ecommerce.product.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse createProduct(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product savedProduct = productRepository.save(product);
        log.info("Created product with id={}", savedProduct.getId());
        return toResponse(savedProduct);
    }

    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id={}", id);
        return toResponse(findProduct(id));
    }

    @Cacheable(value = "products", key = "#minPrice + '-' + #maxPrice")
    public List<ProductResponse> getProducts(BigDecimal minPrice, BigDecimal maxPrice) {
        log.info("Fetching products with minPrice={} and maxPrice={}", minPrice, maxPrice);

        List<Product> products;
        if (minPrice != null && maxPrice != null) {
            products = productRepository.findByPriceBetween(minPrice, maxPrice);
        } else {
            products = productRepository.findAll();
        }

        return products.stream()
                .map(this::toResponse)
                .toList();
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        Product product = findProduct(id);
        product.setName(request.getName());
        product.setPrice(request.getPrice());
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);
        log.info("Updated product with id={}", updatedProduct.getId());
        return toResponse(updatedProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    public ProductResponse updateStock(Long id, StockUpdateRequest request) {
        Product product = findProduct(id);
        product.setStockQuantity(request.getStockQuantity());

        Product updatedProduct = productRepository.save(product);
        log.info("Updated stock for product id={} to {}", id, request.getStockQuantity());
        return toResponse(updatedProduct);
    }

    @CacheEvict(value = "products", allEntries = true)
    public void deleteProduct(Long id) {
        Product product = findProduct(id);
        productRepository.delete(product);
        log.info("Deleted product with id={}", id);
    }

    private Product findProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private ProductResponse toResponse(Product product) {
        ProductResponse response = new ProductResponse();
        response.setId(product.getId());
        response.setName(product.getName());
        response.setPrice(product.getPrice());
        response.setStockQuantity(product.getStockQuantity());
        return response;
    }
}
