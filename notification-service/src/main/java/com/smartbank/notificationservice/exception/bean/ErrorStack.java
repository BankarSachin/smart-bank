package com.smartbank.notificationservice.exception.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Error with code and cause
 * @author Sachin
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"key","stacktrace"})
@Data
@AllArgsConstructor
public class ErrorStack {

	@JsonProperty("key")
	private String key;
	
	@JsonProperty("stacktrace")
	private List<String> stacktrace;
	
	
}
