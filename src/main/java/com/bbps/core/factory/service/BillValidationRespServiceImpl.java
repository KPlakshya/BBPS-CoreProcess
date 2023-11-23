package com.bbps.core.factory.service;

import org.bbps.schema.BillValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.billvalidation.data.BillValidationResponseVO;
import com.bbps.core.constants.ResponseConstants;
import com.bbps.core.entity.service.CustomerReqRespService;
import com.bbps.core.factory.CoreProcess;
import com.bbps.core.kafka.model.Message;
import com.bbps.core.utils.MarshUnMarshUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillValidationRespServiceImpl implements CoreProcess {

	@Autowired
	CustomerReqRespService custReqRespService;

	@Override
	public void process(Message coreReqResp) {
		try {
			String respXMLString = String.valueOf(coreReqResp.getBbpsReqinfo().getMessageBody().getBody());
			BillValidationResponse billValidationResponseType = MarshUnMarshUtil.unmarshal(respXMLString,
					BillValidationResponse.class);
			processBillValidation(billValidationResponseType);
		} catch (Exception e) {
			log.info("Unable to process [{}]", e.getMessage());
		}
	}

	private void processBillValidation(BillValidationResponse resp) {
		try {
			String status = null;
			BillValidationResponseVO validationResp = new BillValidationResponseVO();
			validationResp.setRefId(resp.getHead().getRefId());
			String respCde = resp.getReason().getResponseCode();
			if (ResponseConstants.SUCCESS_CODE.equalsIgnoreCase(respCde)) {
				status = ResponseConstants.SUCCESS_MSG;
				validationResp.setResponseCode(respCde);
				validationResp.setResponseMessage(ResponseConstants.SUCCESS_MSG);
				validationResp.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				validationResp.setComplianceReason(resp.getReason().getComplianceReason());
				validationResp.setApprovalRefNum(resp.getReason().getApprovalRefNum());
				log.info("Bill Fetch Response [{}]",validationResp );
			}else {
				status = ResponseConstants.FAILURE_MSG;
				validationResp.setResponseCode(respCde);
				validationResp.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				validationResp.setComplianceReason(resp.getReason().getComplianceReason());
				validationResp.setResponseMessage(ResponseConstants.FAILURE_MSG);
				
				log.info("Bill Fetch Response [{}]",validationResp );

			}
			custReqRespService.fetchAndUpdate(resp.getHead().getRefId(),
					new ObjectMapper().writeValueAsString(validationResp), status);
			
		}catch (Exception e) {
			log.error("error while saving bill validation response refId[{}] [{}]", resp.getHead().getRefId(),
					e.getMessage());
		}

		
	}
	
	

}
