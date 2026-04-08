package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductRequest;
import com.ecommerce.product.dto.ProductResponse;
import com.ecommerce.product.dto.StockUpdateRequest;
import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void getProducts_shouldReturnFilteredProducts() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(5);

        when(productRepository.findByPriceBetween(new BigDecimal("50.00"), new BigDecimal("150.00")))
                .thenReturn(List.of(product));

        List<ProductResponse> response = productService.getProducts(new BigDecimal("50.00"), new BigDecimal("150.00"));

        assertThat(response).hasSize(1);
        assertThat(response.get(0).getName()).isEqualTo("Phone");
    }

    @Test
    void updateStock_shouldReturnUpdatedProduct() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Phone");
        product.setPrice(new BigDecimal("100.00"));
        product.setStockQuantity(5);

        StockUpdateRequest request = new StockUpdateRequest();
        request.setStockQuantity(10);

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("Phone");
        updated.setPrice(new BigDecimal("100.00"));
        updated.setStockQuantity(10);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updated);

        ProductResponse response = productService.updateStock(1L, request);

        assertThat(response.getStockQuantity()).isEqualTo(10);
    }
}
