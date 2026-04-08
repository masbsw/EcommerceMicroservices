package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exception.OrderCreationException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final RestTemplate restTemplate;
    private final String productServiceUrl;

    public ProductClient(RestTemplate restTemplate,
                         @Value("${product-service.url}") String productServiceUrl) {
        this.restTemplate = restTemplate;
        this.productServiceUrl = productServiceUrl;
    }

    @CircuitBreaker(name = "productService", fallbackMethod = "getProductFallback")
    public ProductResponse getProductById(Long productId) {
        log.info("Calling product-service for product id={}", productId);
        ProductResponse response = restTemplate.getForObject(
                productServiceUrl + "/products/" + productId,
                ProductResponse.class
        );

        if (response == null) {
            throw new OrderCreationException("Product response is empty");
        }

        return response;
    }

    public ProductResponse getProductFallback(Long productId, Exception ex) {
        log.error("Fallback triggered for product id={}", productId, ex);
        throw new OrderCreationException("Product service is unavailable. Please try again later.");
    }
}
