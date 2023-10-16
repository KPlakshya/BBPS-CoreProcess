package com.bbps.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bbps.core.entity.BillPaymentDetails;

public interface BillPaymentDetailsRepo extends JpaRepository<BillPaymentDetails, Long> {

	public BillPaymentDetails findbyRefId(String refId);

}
