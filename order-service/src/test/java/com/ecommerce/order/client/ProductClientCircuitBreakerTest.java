package com.ecommerce.order.client;

import com.ecommerce.order.dto.ProductResponse;
import com.ecommerce.order.exception.OrderCreationException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = ProductClientCircuitBreakerTest.TestApplication.class)
@TestPropertySource(properties = {
        "spring.cloud.config.enabled=false",
        "eureka.client.enabled=false",
        "product-service.url=http://localhost:8082",
        "resilience4j.circuitbreaker.instances.productService.sliding-window-size=5",
        "resilience4j.circuitbreaker.instances.productService.minimum-number-of-calls=3",
        "resilience4j.circuitbreaker.instances.productService.failure-rate-threshold=50",
        "resilience4j.circuitbreaker.instances.productService.wait-duration-in-open-state=10s"
})
class ProductClientCircuitBreakerTest {

    @SpringBootConfiguration
    @EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
    @ComponentScan(basePackageClasses = ProductClient.class)
    static class TestApplication {
    }

    @Autowired
    private ProductClient productClient;

    @Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry;

    @MockBean
    private RestTemplate restTemplate;

    @Test
    void getProductById_shouldUseFallbackWhenProductServiceFails() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenThrow(new RestClientException("Service down"));

        assertThatThrownBy(() -> productClient.getProductById(1L))
                .isInstanceOf(OrderCreationException.class)
                .hasMessage("Product service is unavailable. Please try again later.");
    }

    @Test
    void circuitBreaker_shouldOpenAfterRepeatedFailures() {
        when(restTemplate.getForObject(anyString(), eq(ProductResponse.class)))
                .thenThrow(new RestClientException("Service down"));

        for (int i = 0; i < 3; i++) {
            assertThatThrownBy(() -> productClient.getProductById(1L))
                    .isInstanceOf(OrderCreationException.class);
        }

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("productService");
        assertThat(circuitBreaker.getState()).isEqualTo(CircuitBreaker.State.OPEN);
    }
}
