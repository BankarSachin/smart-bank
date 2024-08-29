package com.smartbank.transactionservice.exception.bean;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Bean used for sending out Standardized error response
 * @author Sachin
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"code","causes","requestcorrelationId"})
@Data
@AllArgsConstructor
public class ErrorInfo {

	@JsonProperty("code")
	private String code;
	
	@JsonProperty("causes")
	private List<String> causes;
	
	@JsonProperty("requestcorrelationId")
	private String requestcorrelationId;
	
	/**
	 * We need this property only for logging purpose
	 */
	@JsonProperty("errorstack")
	@JsonIgnore
	private List<ErrorStack> errorStacks;
}
