package com.task7.payment.client;

import com.task7.payment.dto.ProductResponseDto;
import com.task7.payment.exception.ProductServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductServiceClient {

    private final RestTemplate restTemplate;

    @Value("${product.service.url}")
    private String productServiceUrl;

    private static final String PRODUCTS_ENDPOINT = "/products";

    public List<ProductResponseDto> getProductsByUserId(Long userId) {
        try {
            String url = productServiceUrl + PRODUCTS_ENDPOINT + "/user/" + userId;
            log.debug("Calling Product Service: GET {}", url);

            ProductResponseDto[] products = restTemplate.getForObject(url, ProductResponseDto[].class);
            return Arrays.asList(products);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("User not found: {}", userId);
            throw new ProductServiceException("User not found: " + userId, HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException e) {
            log.error("Client error from Product Service: {}", e.getMessage());
            throw new ProductServiceException("Product Service client error: " + e.getMessage(), e.getStatusCode());
        } catch (HttpServerErrorException e) {
            log.error("Product Service server error: {}", e.getMessage());
            throw new ProductServiceException("Product Service unavailable", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (ResourceAccessException e) {
            log.error("Product Service connection refused: {}", e.getMessage());
            throw new ProductServiceException("Cannot connect to Product Service", HttpStatus.SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            log.error("Unexpected error calling Product Service: {}", e.getMessage());
            throw new ProductServiceException("Product Service error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ProductResponseDto getProductById(Long productId) {
        try {
            String url = productServiceUrl + PRODUCTS_ENDPOINT + "/" + productId;
            log.debug("Calling Product Service: GET {}", url);

            return restTemplate.getForObject(url, ProductResponseDto.class);
        } catch (HttpClientErrorException.NotFound e) {
            log.error("Product not found: {}", productId);
            throw new ProductServiceException("Product not found: " + productId, HttpStatus.NOT_FOUND);
        } catch (HttpClientErrorException e) {
            throw new ProductServiceException("Product Service error: " + e.getMessage(), e.getStatusCode());
        } catch (Exception e) {
            throw new ProductServiceException("Cannot connect to Product Service", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public boolean hasSufficientBalance(Long productId, BigDecimal amount) {
        try {
            String url = productServiceUrl + PRODUCTS_ENDPOINT + "/" + productId + "/balance/sufficient?amount=" + amount;
            log.debug("Calling Product Service: GET {}", url);

            Boolean result = restTemplate.getForObject(url, Boolean.class);
            return result != null && result;
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductServiceException("Product not found: " + productId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ProductServiceException("Cannot check balance", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    public ProductResponseDto withdraw(Long productId, BigDecimal amount) {
        try {
            String url = productServiceUrl + PRODUCTS_ENDPOINT + "/" + productId + "/withdraw?amount=" + amount;
            log.debug("Calling Product Service: POST {}", url);

            return restTemplate.postForObject(url, null, ProductResponseDto.class);
        } catch (HttpClientErrorException.BadRequest e) {
            throw new ProductServiceException("Insufficient balance", HttpStatus.BAD_REQUEST);
        } catch (HttpClientErrorException.NotFound e) {
            throw new ProductServiceException("Product not found: " + productId, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            throw new ProductServiceException("Cannot process withdrawal", HttpStatus.SERVICE_UNAVAILABLE);
        }
    }
}