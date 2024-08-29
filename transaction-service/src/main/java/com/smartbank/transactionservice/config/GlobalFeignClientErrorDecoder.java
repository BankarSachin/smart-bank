package com.smartbank.transactionservice.config;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import com.google.gson.Gson;
import com.smartbank.transactionservice.exception.ExceptionCode;
import com.smartbank.transactionservice.exception.TxnException;
import com.smartbank.transactionservice.exception.bean.ErrorInfo;

import feign.Response;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GlobalFeignClientErrorDecoder implements ErrorDecoder {
	private ErrorDecoder errorDecoder = new Default();
    /**
     * Decodes the response and returns an Exception based on the status code.
     *
     * @param s        The string representation of the response.
     * @param response The HTTP response.
     * @return An Exception based on the status code.
     */
    @Override
    public Exception decode(String methodKey, Response response) {
    	if (response.body()==null) {
    		log.error("decode - Empty response received from notification ervice");
			return new TxnException(ExceptionCode.TXNS_RMTE_SERVICE_EMPTY_RESPONSE); 
		}
        if (response.status()!=HttpStatus.SC_OK || response.status()!=HttpStatus.SC_CREATED) {
			return extractException(response);
		}else {
			 return errorDecoder.decode(methodKey, response);
		}
    }

    /**
     * Extracts a AccsException object from the response body.
     *
     * @param response the response object
     * @return the AccsException object extracted from the response body
     */
    private TxnException extractException(Response response) {
    	final String methodName = "extractException";
    	TxnException globalException = null;

        try(Reader reader = response.body().asReader(StandardCharsets.UTF_8)) {
            String result = IOUtils.toString(reader);
            ErrorInfo errorInfo = new Gson().fromJson(result, ErrorInfo.class);
			log.info("{} - Error received from notification service {} : {}", methodName, errorInfo.getCode(),errorInfo.getCauses());
			globalException = new TxnException(ExceptionCode.TXNS_RMTE_SERVICE_EXCEPTION, errorInfo.getCode(),errorInfo.getCauses().get(0));
        } catch (IOException e) {
            log.error("IO exception on reading exception message", e);
        } 
        return globalException;
    }
}