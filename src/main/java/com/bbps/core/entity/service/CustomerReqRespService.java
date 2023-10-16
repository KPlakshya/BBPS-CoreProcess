package com.bbps.core.entity.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.core.entity.CustomerRequestResponse;
import com.bbps.core.repository.CustomerReqRespRepo;

@Service
public class CustomerReqRespService {

	@Autowired
	private CustomerReqRespRepo repo;

	public CustomerRequestResponse findByRefId(String refId) {
		return repo.findByRefId(refId);
	}

	public CustomerRequestResponse fetchAndUpdate(String refId, String response, String status) {
		CustomerRequestResponse custreqresp = findByRefId(refId);
		custreqresp.setResponse(response);
		custreqresp.setResponseTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		custreqresp.setStatus(status);
		repo.save(custreqresp);
		return custreqresp;
	}

}
