package com.bbps.core.factory.service;

import java.util.ArrayList;
import java.util.List;

import org.bbps.schema.Biller;
import org.bbps.schema.BillerFetchResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bbps.billerfetch.data.AmountOption;
import com.bbps.billerfetch.data.BillerCustomerParam;
import com.bbps.billerfetch.data.BillerFetchResponseVO;
import com.bbps.billerfetch.data.InterchangeFee;
import com.bbps.billerfetch.data.InterchangeFeeConf;
import com.bbps.billerfetch.data.InterchangeFeeDetail;
import com.bbps.billerfetch.data.PaymentChannel;
import com.bbps.billerfetch.data.PaymentMode;
import com.bbps.core.constants.ResponseConstants;
import com.bbps.core.entity.service.CustomerReqRespService;
import com.bbps.core.factory.CoreProcess;
import com.bbps.core.kafka.model.Message;
import com.bbps.core.utils.MarshUnMarshUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BillerFetchRespServiceImpl implements CoreProcess {

	@Autowired
	CustomerReqRespService custReqRespService;

	@Override
	public void process(Message coreReqResp) {

		try {
			String respXMLString = String.valueOf(coreReqResp.getBbpsReqinfo().getMessageBody().getBody());
			BillerFetchResponse billFetchResponseType = MarshUnMarshUtil.unmarshal(respXMLString,
					BillerFetchResponse.class);
			processBillerFetch(billFetchResponseType);
		} catch (Exception e) {
			log.info("Unable to process [{}]", e.getMessage());
		}

	}

	private void processBillerFetch(BillerFetchResponse resp) {
		try {
			String status = null;
			BillerFetchResponseVO billerFetchResponse = new BillerFetchResponseVO();
			billerFetchResponse.setRefId(resp.getHead().getRefId());
			if (resp.getBillers() != null) {
				Biller biller = resp.getBillers().get(0);
				status = ResponseConstants.SUCCESS_MSG;
				billerFetchResponse.setResponseCode(ResponseConstants.SUCCESS_CODE);
				billerFetchResponse.setResponseMessage(ResponseConstants.SUCCESS_MSG);
				billerFetchResponse.setBillerId(biller.getBillerId());
				billerFetchResponse.setBillerName(biller.getBillerName());
				billerFetchResponse.setBillerAliasName(biller.getBillerAliasName());
				billerFetchResponse.setBillerCategoryName(biller.getBillerCategoryName());
				billerFetchResponse.setBillerMode(biller.getBillerMode());
				billerFetchResponse.setBillerAcceptsAdhoc(biller.isBillerAcceptsAdhoc());
				billerFetchResponse.setParentBiller(biller.isParentBiller());
				billerFetchResponse.setBillerOwnerShp(biller.getBillerOwnerShp());
				billerFetchResponse.setBillerCoverage(biller.getBillerCoverage());
				billerFetchResponse.setFetchRequirement(biller.getFetchRequirement().value());
				billerFetchResponse.setPaymentAmountExactness(biller.getPaymentAmountExactness());
				billerFetchResponse.setSupportBillValidation(biller.getSupportBillValidation());
				billerFetchResponse.setBillerEffctvFrom(biller.getBillerEffctvFrom());
				billerFetchResponse.setBillerEffctvTo(biller.getBillerEffctvTo());
				billerFetchResponse.setBillerTempDeactivationStart(biller.getBillerTempDeactivationStart());
				billerFetchResponse.setBillerTempDeactivationEnd(biller.getBillerTempDeactivationEnd());
				if (biller.getBillerPaymentModes() != null) {
					List<PaymentMode> paymentModes = new ArrayList<PaymentMode>();
					for (int i = 0; i < biller.getBillerPaymentModes().size(); i++) {
						PaymentMode paymentMode = new PaymentMode();
						paymentMode.setPaymentMode(biller.getBillerPaymentModes().get(i).getPaymentMode());
						paymentMode.setMinLimit(biller.getBillerPaymentModes().get(i).getMinLimit());
						paymentMode.setSupportPendingStatus(
								biller.getBillerPaymentModes().get(i).getSupportPendingStatus());
						paymentModes.add(paymentMode);
					}
					billerFetchResponse.setBillerPaymentModes(paymentModes);
				}

				if (biller.getBillerPaymentChannels() != null) {
					List<PaymentChannel> paymentChannes = new ArrayList<PaymentChannel>();
					for (int i = 0; i < biller.getBillerPaymentChannels().size(); i++) {
						PaymentChannel paymentChannel = new PaymentChannel();
						paymentChannel.setPaymentChannel(biller.getBillerPaymentChannels().get(i).getPaymentChannel());
						paymentChannel.setMinLimit(biller.getBillerPaymentChannels().get(i).getMinLimit());
						paymentChannel.setSupportPendingStatus(
								biller.getBillerPaymentChannels().get(i).getSupportPendingStatus());
						paymentChannes.add(paymentChannel);
					}
					billerFetchResponse.setBillerPaymentChannels(paymentChannes);
				}

				if (biller.getBillerCustomerParams() != null) {
					List<BillerCustomerParam> billerCustomerParams = new ArrayList<BillerCustomerParam>();
					for (int i = 0; i < biller.getBillerCustomerParams().size(); i++) {
						BillerCustomerParam billerCustomerParam = new BillerCustomerParam();
						billerCustomerParam.setParamName(biller.getBillerCustomerParams().get(i).getParamName());
						billerCustomerParam.setDataType(biller.getBillerCustomerParams().get(i).getDataType().value());
					//	billerCustomerParam.setMaxLength(biller.getBillerCustomerParams().get(i).getMaxLength());
					//	billerCustomerParam.setMinLength(biller.getBillerCustomerParams().get(i).getMinLength());
						billerCustomerParam.setOptional(biller.getBillerCustomerParams().get(i).isOptional());
						billerCustomerParam.setRegex(biller.getBillerCustomerParams().get(i).getRegex());
					//	billerCustomerParam.setVisibility(biller.getBillerCustomerParams().get(i).isVisibility());
						billerCustomerParams.add(billerCustomerParam);
					}
					billerFetchResponse.setBillerCustomerParams(billerCustomerParams);
				}
//				if(biller.getCustomerParamGroups() != null && biller.getCustomerParamGroups().getGroup() != null) {
//					CustomerParamGroups custParamGroups = new CustomerParamGroups(); 
//					for (int i = 0; i < biller.getCustomerParamGroups().getGroup().size(); i++) {
//						List<Group> grps = biller.getCustomerParamGroups().getGroup();
//						for (int j = 0; j < grps.size(); j++) {
//							List<Group> grpsub = biller.getCustomerParamGroups().getGroup().get(j).getGroup();
//							for (int k = 0; k < grpsub.size(); j++) {
//								com.bbps.billerfetch.data.Group respGroup = new com.bbps.billerfetch.data.Group();
//								respGroup.set
//							
//							}
//						
//						}
//							
//							
//						}
//						
//					}

				if (biller.getBillerResponseParams() != null
						&& biller.getBillerResponseParams().getAmountOptions() != null) {
					List<AmountOption> amountoptions = new ArrayList<AmountOption>();
					for (int i = 0; i < biller.getBillerResponseParams().getAmountOptions().size(); i++) {
						AmountOption amountOption = new AmountOption();
						List<String> amountBreakupSet = new ArrayList<String>();
						for (int j = 0; j < biller.getBillerResponseParams().getAmountOptions().get(i)
								.getAmountBreakupSets().size(); j++) {
							amountBreakupSet.add(biller.getBillerResponseParams().getAmountOptions().get(i)
									.getAmountBreakupSets().get(j));
						}
						amountOption.setAmountBreakupSet(amountBreakupSet);
						amountoptions.add(amountOption);
					}
					com.bbps.billerfetch.data.BillerResponseParams billerResponseParams = new com.bbps.billerfetch.data.BillerResponseParams();

					billerResponseParams.setAmountOptions(amountoptions);
					billerFetchResponse.setBillerResponseParams(billerResponseParams);

				}

				if (biller.getBillerAdditionalInfos() != null) {
					List<BillerCustomerParam> billerCustomerParams = new ArrayList<BillerCustomerParam>();
					for (int i = 0; i < biller.getBillerAdditionalInfos().size(); i++) {
						BillerCustomerParam billerCustomerParam = new BillerCustomerParam();
						billerCustomerParam.setParamName(biller.getBillerAdditionalInfos().get(i).getParamName());
						billerCustomerParam.setDataType(biller.getBillerAdditionalInfos().get(i).getDataType().value());
					//	billerCustomerParam.setMaxLength(biller.getBillerAdditionalInfos().get(i).getMaxLength());
					//	billerCustomerParam.setMinLength(biller.getBillerAdditionalInfos().get(i).getMinLength());
						billerCustomerParam.setOptional(biller.getBillerAdditionalInfos().get(i).isOptional());
						billerCustomerParam.setRegex(biller.getBillerAdditionalInfos().get(i).getRegex());
//						billerCustomerParam
//								.setVisibility(biller.getBillerAdditionalInfos().get(i).isVisibility());
						billerCustomerParams.add(billerCustomerParam);
					}
					billerFetchResponse.setBillerAdditionalInfo(billerCustomerParams);
				}

				if (biller.getBillerAdditionalInfoPayments() != null) {
					List<BillerCustomerParam> billerCustomerParams = new ArrayList<BillerCustomerParam>();
					for (int i = 0; i < biller.getBillerAdditionalInfoPayments().size(); i++) {
						BillerCustomerParam billerCustomerParam = new BillerCustomerParam();
						billerCustomerParam.setParamName(biller.getBillerAdditionalInfoPayments().get(i).getParamName());
						billerCustomerParam
								.setDataType(biller.getBillerAdditionalInfoPayments().get(i).getDataType().value());
						//billerCustomerParam.setMaxLength(biller.getBillerAdditionalInfoPayments().get(i).getMaxLength());
						//billerCustomerParam.setMinLength(biller.getBillerAdditionalInfoPayments().get(i).getMinLength());
						billerCustomerParam.setOptional(biller.getBillerAdditionalInfoPayments().get(i).isOptional());
						billerCustomerParam.setRegex(biller.getBillerAdditionalInfoPayments().get(i).getRegex());
//						billerCustomerParam
//								.setVisibility(biller.getBillerAdditionalInfoPayments().get(i).isVisibility());
						billerCustomerParams.add(billerCustomerParam);
					}
					billerFetchResponse.setBillerAdditionalInfoPayment(billerCustomerParams);
				}

				if (biller.getInterchangeFeeConves() != null) {
					List<InterchangeFeeConf> interchangeFeeConfs = new ArrayList<InterchangeFeeConf>();
					for (int i = 0; i < biller.getInterchangeFeeConves().size(); i++) {
						InterchangeFeeConf interchangeFeeConf = new InterchangeFeeConf();
						interchangeFeeConf.setDefaultFee(biller.getInterchangeFeeConves().get(i).isDefaultFee());
						interchangeFeeConf.setEffctvFrom(biller.getInterchangeFeeConves().get(i).getEffctvFrom());
						interchangeFeeConf.setFees(biller.getInterchangeFeeConves().get(i).getFees());
						interchangeFeeConf.setMti(biller.getInterchangeFeeConves().get(i).getMti());
						interchangeFeeConf.setPaymentChannel(biller.getInterchangeFeeConves().get(i).getPaymentChannel());
						interchangeFeeConf.setPaymentMode(biller.getInterchangeFeeConves().get(i).getPaymentMode());
						interchangeFeeConf.setResponseCode(biller.getInterchangeFeeConves().get(i).getResponseCode());
						interchangeFeeConf.setEffctvTo(biller.getInterchangeFeeConves().get(i).getEffctvTo());
						interchangeFeeConfs.add(interchangeFeeConf);
					}
					billerFetchResponse.setInterchangeFeeConf(interchangeFeeConfs);
				}

				if (biller.getInterchangeFees() != null) {
					List<InterchangeFee> interchangeFees = new ArrayList<InterchangeFee>();
					for (int i = 0; i < biller.getInterchangeFees().size(); i++) {
						InterchangeFee interchangeFee = new InterchangeFee();
						interchangeFee.setFeeCode(biller.getInterchangeFees().get(i).getFeeCode());
						interchangeFee.setFeeDesc(biller.getInterchangeFees().get(i).getFeeDesc());
						interchangeFee.setFeeDirection(biller.getInterchangeFees().get(i).getFeeDirection().value());
						List<InterchangeFeeDetail> interchangeFeeDetais = new ArrayList<InterchangeFeeDetail>();
						for (int j = 0; j < biller.getInterchangeFees().get(i).getInterchangeFeeDetails().size(); j++) {
							InterchangeFeeDetail interchangeFeeDetail = new InterchangeFeeDetail();
							interchangeFeeDetail.setEffctvFrom(biller.getInterchangeFees().get(i)
									.getInterchangeFeeDetails().get(j).getEffctvFrom());
							interchangeFeeDetail.setEffctvTo(
									biller.getInterchangeFees().get(i).getInterchangeFeeDetails().get(j).getEffctvTo());
							interchangeFeeDetail.setFlatFee(
									biller.getInterchangeFees().get(i).getInterchangeFeeDetails().get(j).getFlatFee());
							interchangeFeeDetail.setPercentFee(biller.getInterchangeFees().get(i)
									.getInterchangeFeeDetails().get(j).getPercentFee());
							interchangeFeeDetail.setTranAmtRangeMax(biller.getInterchangeFees().get(i)
									.getInterchangeFeeDetails().get(j).getTranAmtRangeMax());
							interchangeFeeDetail.setTranAmtRangeMin(biller.getInterchangeFees().get(i)
									.getInterchangeFeeDetails().get(j).getTranAmtRangeMin());
							interchangeFeeDetais.add(interchangeFeeDetail);
						}
						interchangeFees.add(interchangeFee);
					}
					billerFetchResponse.setInterchangeFee(interchangeFees);
				}
				billerFetchResponse.setStatus(biller.getStatus());
				billerFetchResponse.setBillerDescription(biller.getBillerDescription());
				billerFetchResponse.setSupportDeemed(biller.getSupportDeemed());
				billerFetchResponse.setSupportPendingStatus(biller.getSupportPendingStatus());
				billerFetchResponse.setBillerTimeOut(biller.getBillerTimeOut());
				billerFetchResponse.setPlanMdmRequirement(biller.getPlanMdmRequirement().value());

				if (biller.getPlanAdditionalInfos() != null) {
					List<BillerCustomerParam> billerCustomerParams = new ArrayList<BillerCustomerParam>();
					for (int i = 0; i < biller.getPlanAdditionalInfos().size(); i++) {
						BillerCustomerParam billerCustomerParam = new BillerCustomerParam();
						billerCustomerParam.setParamName(biller.getPlanAdditionalInfos().get(i).getParamName());
						billerCustomerParam.setDataType(biller.getPlanAdditionalInfos().get(i).getDataType().value());
						//billerCustomerParam.setMaxLength(biller.getPlanAdditionalInfos().get(i).getMaxLength());
						//billerCustomerParam.setMinLength(biller.getPlanAdditionalInfos().get(i).getMinLength());
						billerCustomerParam.setOptional(biller.getPlanAdditionalInfos().get(i).isOptional());
						billerCustomerParam.setRegex(biller.getPlanAdditionalInfos().get(i).getRegex());
					//	billerCustomerParam.setVisibility(biller.getPlanAdditionalInfos().get(i).isVisibility());
						billerCustomerParams.add(billerCustomerParam);
					}
					billerFetchResponse.setPlanAdditionalInfo(billerCustomerParams);
				}

			} else {
				status = ResponseConstants.FAILURE_MSG;
				billerFetchResponse.setResponseCode(ResponseConstants.FAILURE_CODE);
				billerFetchResponse.setResponseMessage(ResponseConstants.FAILURE_MSG);
			}

			custReqRespService.fetchAndUpdate(resp.getHead().getRefId(),
					new ObjectMapper().writeValueAsString(billerFetchResponse), status);

		} catch (Exception e) {
			e.printStackTrace();
			log.error("error while saving bill fetch response refId[{}] [{}]", resp.getHead().getRefId(),
					e.getMessage());
		}

	}
}
