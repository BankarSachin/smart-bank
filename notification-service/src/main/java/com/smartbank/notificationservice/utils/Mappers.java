package com.smartbank.notificationservice.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import com.smartbank.notificationservice.dto.NotificationRequest;

public class Mappers {

	public static final BiFunction<String,NotificationRequest, Map<String,Object>> fnMappper = (accountNumber, notificationReq) ->{
		Map<String, Object> map = new HashMap<>();
		map.put("accountNumber", DataMaskingUtil.maskAccountNumber(accountNumber));
		map.put("txnDateTime", notificationReq.getTxnDateTime());
		map.put("txnAmount", notificationReq.getTxnAmmount());
		map.put("closingBalance", notificationReq.getCurrentBalance());
		map.put("utrNumber", notificationReq.getUtrNumber());
		return map;
	};
			
	public static final BiFunction<String,NotificationRequest, Map<String,Object>> fnTransferMappper = (accountNumber, notificationReq) ->{
		Map<String, Object> map = new HashMap<>();
		map.put("accountNumber", DataMaskingUtil.maskAccountNumber(accountNumber));
		map.put("txnDateTime", notificationReq.getTxnDateTime());
		map.put("txnAmount", notificationReq.getTxnAmmount());
		map.put("closingBalance", notificationReq.getCurrentBalance());
		map.put("utrNumber", notificationReq.getUtrNumber());
		map.put("destinationAccountNumber", notificationReq.getDestinationAccountNumber());
		return map;
	};
		
}
