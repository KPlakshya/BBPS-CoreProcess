package com.bbps.core.entity.service;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.core.entity.CustomerRequestResponse;
import com.bbps.core.repository.CustomerReqRespRepo;

@Service
@Slf4j
public class CustomerReqRespService {

	@Autowired
	private CustomerReqRespRepo repo;

	public CustomerRequestResponse findByRefId(String refId) {
		return repo.findByRefId(refId);
	}

	public CustomerRequestResponse fetchAndUpdate(String refId, String response, String status) {
		log.info("Fetch Nd Update RefID {}",refId);
		CustomerRequestResponse custreqresp = findByRefId(refId);
		log.info("Response From table {} ",custreqresp);
		custreqresp.setResponse(response);
		custreqresp.setResponseTimestamp(Timestamp.valueOf(LocalDateTime.now()));
		custreqresp.setStatus(status);
		repo.save(custreqresp);
		return custreqresp;
	}

}
