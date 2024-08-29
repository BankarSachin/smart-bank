package com.smartbank.accountservice.factory;

import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.core5.util.Timeout;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class RestClientFactory {

	@Bean
	public RequestConfig requestConfig() {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(Timeout.ofSeconds(10))       // Connection timeout
                .setResponseTimeout(Timeout.ofSeconds(30))      // Socket timeout (waiting for data)
                .setConnectionRequestTimeout(Timeout.ofSeconds(5)) // Timeout to get a connection from the pool
                .setRedirectsEnabled(true)                     // Allow redirects
                .build();
	}
	
	@Bean
	public PoolingHttpClientConnectionManager customizedPoolingHttpClientConnectionManager() {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        connectionManager.setMaxTotal(20); // Max total connections
        connectionManager.setDefaultMaxPerRoute(5); // Max connections per route
		return connectionManager;
	}
	
}