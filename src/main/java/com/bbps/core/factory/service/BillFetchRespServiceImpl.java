package com.bbps.core.factory.service;

import java.util.ArrayList;
import java.util.List;

import org.bbps.schema.BillFetchResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.billfetch.data.BillFetchResponse;
import com.bbps.billfetch.data.Input;
import com.bbps.billfetch.data.InputParams;
import com.bbps.billfetch.data.Tag;
import com.bbps.core.constants.ResponseConstants;
import com.bbps.core.entity.service.CustomerReqRespService;
import com.bbps.core.factory.CoreProcess;
import com.bbps.core.kafka.model.Message;
import com.bbps.core.utils.MarshUnMarshUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillFetchRespServiceImpl implements CoreProcess {

	@Autowired
	CustomerReqRespService custReqRespService;

	@Override
	public void process(Message coreReqResp) {

		try {
			String respXMLString = String.valueOf(coreReqResp.getBbpsReqinfo().getMessageBody().getBody());
			BillFetchResponseType billFetchResponseType = MarshUnMarshUtil.unmarshal(respXMLString,
					BillFetchResponseType.class);
			processBillFetch(billFetchResponseType);
		} catch (Exception e) {
			log.info("Unable to process [{}]", e.getMessage());
		}
	}

	public void processBillFetch(BillFetchResponseType resp) {
		
		try {
			String status = null;
			BillFetchResponse billFetchResponse = new BillFetchResponse();
			billFetchResponse.setRefId(resp.getHead().getRefId());
			String respCde = resp.getReason().getResponseCode();
			if (ResponseConstants.SUCCESS_CODE.equalsIgnoreCase(respCde)) {
				status = ResponseConstants.SUCCESS_MSG;
				billFetchResponse.setResponseCode(respCde);
				billFetchResponse.setResponseMessage(ResponseConstants.SUCCESS_MSG);
				billFetchResponse.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				billFetchResponse.setComplianceReason(resp.getReason().getComplianceReason());
				billFetchResponse.setApprovalRefNum(resp.getReason().getApprovalRefNum());
				billFetchResponse.setCustomerName(resp.getBillerResponse().getCustomerName());
				billFetchResponse.setAmount(resp.getBillerResponse().getAmount());
				billFetchResponse.setDueDate(resp.getBillerResponse().getDueDate());
				billFetchResponse.setBillDate(resp.getBillerResponse().getBillDate());
				billFetchResponse.setBillPeriod(resp.getBillerResponse().getBillPeriod());
				billFetchResponse.setBillNumber(resp.getBillerResponse().getBillNumber());

				// Cutomer param Iterate into Input param in pojo
				if (resp.getBillDetails().getCustomerParams() != null) {
					InputParams inputparams = new InputParams();
					List<Input> inputs = new ArrayList<Input>();
					for (int i = 0; i < resp.getBillDetails().getCustomerParams().getTag().size(); i++) {
						Input input = new Input();
						input.setParamName(resp.getBillDetails().getCustomerParams().getTag().get(i).getName());
						input.setParamValue(resp.getBillDetails().getCustomerParams().getTag().get(i).getValue());
						inputs.add(input);
					}
					billFetchResponse.setInputParams(inputparams);
				}
				// Biller Response tag to bill response tag
				if (resp.getBillerResponse() != null) {

					List<Tag> tags = new ArrayList<Tag>();
					for (int i = 0; i < resp.getBillerResponse().getTag().size(); i++) {
						Tag tag = new Tag();
						tag.setName(resp.getBillerResponse().getTag().get(i).getName());
						tag.setValue(resp.getBillerResponse().getTag().get(i).getValue());
						tags.add(tag);
					}
					billFetchResponse.setBillerResponseTag(tags);
				}
				// Additional Info to addtional info of pojo
				if (resp.getAdditionalInfo() != null) {

					List<Tag> tags = new ArrayList<Tag>();
					for (int i = 0; i < resp.getAdditionalInfo().getTag().size(); i++) {
						Tag tag = new Tag();
						tag.setName(resp.getBillerResponse().getTag().get(i).getName());
						tag.setValue(resp.getBillerResponse().getTag().get(i).getValue());
						tags.add(tag);
					}
					billFetchResponse.setAdditionaInfo(tags);
				}

			} else {
				status = ResponseConstants.FAILURE_MSG;
				billFetchResponse.setResponseCode(respCde);
				billFetchResponse.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				billFetchResponse.setComplianceReason(resp.getReason().getComplianceReason());
				billFetchResponse.setResponseMessage(ResponseConstants.FAILURE_MSG);
				
				log.info("Bill Fetch Response [{}]",billFetchResponse );

			}
			custReqRespService.fetchAndUpdate(resp.getHead().getRefId(),
					new ObjectMapper().writeValueAsString(billFetchResponse), status);
		} catch (Exception e) {
			log.error("error while saving bill fetch response refId[{}] [{}]", resp.getHead().getRefId(),
					e.getMessage());
		}
	
			
		

	}

}
