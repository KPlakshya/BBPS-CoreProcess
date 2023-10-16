package com.bbps.core.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import com.bbps.core.entity.CustomerRequestResponse;



@Component
public interface CustomerReqRespRepo extends JpaRepository<CustomerRequestResponse,Long> {
	
	public CustomerRequestResponse findByRefId(String refId);

}
