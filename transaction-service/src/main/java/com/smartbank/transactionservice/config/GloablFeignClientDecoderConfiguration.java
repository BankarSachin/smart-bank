package com.smartbank.transactionservice.config;

import org.springframework.cloud.openfeign.FeignClientProperties;
import org.springframework.context.annotation.Configuration;

import feign.codec.ErrorDecoder;

@Configuration
public class GloablFeignClientDecoderConfiguration extends FeignClientProperties.FeignClientConfiguration {

    /**
     * Returns an instance of ErrorDecoder that will be used to decode errors returned by Feign clients.
     *
     * @return the ErrorDecoder instance
     */
    public ErrorDecoder errorDecoder() {
        return new GlobalFeignClientErrorDecoder();
    }
}
