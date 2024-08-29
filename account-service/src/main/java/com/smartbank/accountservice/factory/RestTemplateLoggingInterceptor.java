package com.smartbank.accountservice.factory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

/**
 * Interceptor added for testng purpose
 * @author Sachin
 */
public class RestTemplateLoggingInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateLoggingInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logRequestDetails(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        logResponseDetails(response);
        return response;
    }

    private void logRequestDetails(HttpRequest request, byte[] body) throws IOException {
        logger.info("URI: {}", request.getURI());
        logger.info("Method: {}", request.getMethod());
        logger.info("Headers: {}", request.getHeaders());
        logger.info("Request Body: {}", new String(body, StandardCharsets.UTF_8));
    }

    private void logResponseDetails(ClientHttpResponse response) throws IOException {
        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Headers: {}", response.getHeaders());
        logger.info("Response Body: {}", StreamUtils.copyToString(response.getBody(), StandardCharsets.UTF_8));
    }
}
