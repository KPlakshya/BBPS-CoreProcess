package com.bbps.core.factory.service;

import java.util.ArrayList;
import java.util.List;

import org.bbps.schema.BillPaymentResponseType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.billfetch.data.Input;
import com.bbps.billpayment.data.AdditionalInfo;
import com.bbps.billpayment.data.BillDetails;
import com.bbps.billpayment.data.BillPaymentResponse;
import com.bbps.billpayment.data.BillerResponse;
import com.bbps.billpayment.data.InputParams;
import com.bbps.billpayment.data.Tag;
import com.bbps.core.constants.ResponseConstants;
import com.bbps.core.entity.service.BillPaymentDetailsService;
import com.bbps.core.entity.service.CustomerReqRespService;
import com.bbps.core.factory.CoreProcess;
import com.bbps.core.kafka.model.Message;
import com.bbps.core.utils.MarshUnMarshUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillPaymentRespServiceImpl implements CoreProcess {

	@Autowired
	private CustomerReqRespService custReqRespService;
	@Autowired
	private BillPaymentDetailsService billPaymentDetailsService;

	@Override
	public void process(Message coreReqResp) {
		try {
			String respXMLString = String.valueOf(coreReqResp.getBbpsReqinfo().getMessageBody().getBody());
			BillPaymentResponseType BillPaymentResponseType = MarshUnMarshUtil.unmarshal(respXMLString,
					BillPaymentResponseType.class);
			processBillPayment(BillPaymentResponseType);
		} catch (Exception e) {
			log.info("Unable to process [{}]", e.getMessage());
		}
	}

	private void processBillPayment(BillPaymentResponseType resp) {
		try {
			String status = null;
			BillPaymentResponse payresp = new BillPaymentResponse();
			payresp.setRefId(resp.getHead().getRefId());
			String respCde = resp.getReason().getResponseCode();
			if (ResponseConstants.SUCCESS_CODE.equalsIgnoreCase(respCde)) {
				status = ResponseConstants.SUCCESS_MSG;
				payresp.setSiTxn(resp.getHead().getSiTxn().value());
				payresp.setOrigRefId(resp.getHead().getOrigRefId());
				payresp.setResponseCode(respCde);
				payresp.setResponseMessage(resp.getReason().getResponseCode());
				payresp.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				payresp.setComplianceReason(resp.getReason().getComplianceReason());
				payresp.setApprovalRefNum(resp.getReason().getApprovalRefNum());
				payresp.setTxnReferenceId(resp.getTxn().getTxnReferenceId());
				payresp.setTxnMsgId(resp.getTxn().getMsgId());
				payresp.setTxnTs(resp.getTxn().getTs());
				BillDetails detials = new BillDetails();
				detials.setBillerId(resp.getBillDetails().getBiller().getId());
				if (resp.getBillDetails().getCustomerParams() != null) {
					InputParams inputparams = new InputParams();
					List<Input> inputs = new ArrayList<Input>();
					for (int i = 0; i < resp.getBillDetails().getCustomerParams().getTag().size(); i++) {
						Input input = new Input();
						input.setParamName(resp.getBillDetails().getCustomerParams().getTag().get(i).getName());
						input.setParamValue(resp.getBillDetails().getCustomerParams().getTag().get(i).getValue());
						inputs.add(input);
					}
					detials.setInputParams(inputparams);
				}
				payresp.setBillDetails(detials);
				if (resp.getBillerResponse() != null) {
					BillerResponse billresp = new BillerResponse();
					billresp.setCustomerName(resp.getBillerResponse().getCustomerName());
					billresp.setAmount(resp.getBillerResponse().getAmount());
					billresp.setDueDate(resp.getBillerResponse().getDueDate());
					billresp.setBillDate(resp.getBillerResponse().getBillDate());
					billresp.setBillNumber(resp.getBillerResponse().getBillNumber());
					billresp.setBillPeriod(resp.getBillerResponse().getBillPeriod());
					if (resp.getBillerResponse().getTag() != null) {
						List<Tag> inputs = new ArrayList<Tag>();
						for (int i = 0; i < resp.getBillerResponse().getTag().size(); i++) {
							Tag input = new Tag();
							input.setName(resp.getBillerResponse().getTag().get(i).getName());
							input.setValue(resp.getBillerResponse().getTag().get(i).getValue());
							inputs.add(input);
						}
						billresp.setTags(inputs);
					}
					payresp.setBillerResponse(billresp);
				}
				if (resp.getAdditionalInfo() != null) {

					List<AdditionalInfo> tags = new ArrayList<AdditionalInfo>();
					for (int i = 0; i < resp.getAdditionalInfo().getTag().size(); i++) {
						AdditionalInfo tag = new AdditionalInfo();
						tag.setName(resp.getBillerResponse().getTag().get(i).getName());
						tag.setValue(resp.getBillerResponse().getTag().get(i).getValue());
						tags.add(tag);
					}
					payresp.setAdditionaInfo(tags);
				}

			} else {
				status = ResponseConstants.FAILURE_MSG;
				payresp.setResponseCode(respCde);
				payresp.setComplianceRespCd(resp.getReason().getComplianceRespCd());
				payresp.setComplianceReason(resp.getReason().getComplianceReason());
				payresp.setResponseMessage(ResponseConstants.FAILURE_MSG);

				log.info("Bill Payment Response [{}]", payresp);

			}

			billPaymentDetailsService.fetchandUpdate(payresp, status);

			custReqRespService.fetchAndUpdate(resp.getHead().getRefId(), new ObjectMapper().writeValueAsString(payresp),
					status);
		} catch (Exception e) {
			log.error("error while saving bill payment response refId[{}] [{}]", resp.getHead().getRefId(),
					e.getMessage());
		}

	}

}
