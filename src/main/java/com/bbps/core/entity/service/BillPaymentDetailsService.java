package com.bbps.core.entity.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.billpayment.data.BillPaymentResponse;
import com.bbps.core.entity.BillPaymentDetails;
import com.bbps.core.repository.BillPaymentDetailsRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillPaymentDetailsService {

	@Autowired
	private BillPaymentDetailsRepo repo;

	public void fetchandUpdate(BillPaymentResponse payresp, String status) {
		BillPaymentDetails billPaymentDetails = repo.findbyRefId(payresp.getRefId());
		if (billPaymentDetails != null) {
			billPaymentDetails.setResponseCode(payresp.getResponseCode());
			billPaymentDetails.setResponseMessage(payresp.getResponseMessage());
			billPaymentDetails.setStatus(status);
			billPaymentDetails.setResponseTimestamp(Timestamp.valueOf(LocalDateTime.now()));

			repo.save(billPaymentDetails);
		} else {
			log.error("No request found for the refId{}", payresp.getRefId());
		}

	}

}
