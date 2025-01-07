package com.website.api.mosi.api.rest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class GenericHttpClient {

    private final RestTemplate restTemplate;

    public GenericHttpClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> sendRequest(String url, HttpMethod method, Map<String, String> headers, String body) {
        // Set headers
        HttpHeaders httpHeaders = new HttpHeaders();
        headers.forEach(httpHeaders::set);

        // Create HTTP entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(body, httpHeaders);

        // Execute the request
        return restTemplate.exchange(url, method, requestEntity, String.class);
    }
}

