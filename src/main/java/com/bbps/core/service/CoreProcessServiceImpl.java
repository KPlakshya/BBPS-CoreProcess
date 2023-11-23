package com.bbps.core.service;

import com.bbps.core.constants.CoreContsants;
import com.bbps.core.factory.CoreProcess;
import com.bbps.core.factory.service.BillFetchRespServiceImpl;
import com.bbps.core.factory.service.BillPaymentRespServiceImpl;
import com.bbps.core.factory.service.BillValidationRespServiceImpl;
import com.bbps.core.factory.service.BillerFetchRespServiceImpl;
import com.bbps.core.kafka.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Service
@Slf4j
public class CoreProcessServiceImpl implements CoreProcessService {

    @Value("${customer.posting.request}")
    String customerPostingReqTopic;

    @Autowired
    BillFetchRespServiceImpl billFetchRespService;

    @Autowired
    BillerFetchRespServiceImpl billerFetchRespService;


    @Autowired
    BillPaymentRespServiceImpl billPaymentRespService;


    @Autowired
    BillValidationRespServiceImpl billValidationRespService;

    public static final Map<String, CoreProcess> serviceImpl = new HashMap<String, CoreProcess>();

    @PostConstruct
    public void init() {
        log.info("loading service class");
        serviceImpl.put(CoreContsants.BILL_FETCH_RESPONSE, billFetchRespService);
        serviceImpl.put(CoreContsants.BILLER_FETCH_RESPONSE, billerFetchRespService);
        serviceImpl.put(CoreContsants.BILL_PAYMENT_RESPONSE, billPaymentRespService);
        serviceImpl.put(CoreContsants.BILL_VALIDATION_RESPONSE, billValidationRespService);


    }
    @Override
    public void findReqType(Message coreProcessReqResp) {
        String requestType = String.valueOf(coreProcessReqResp.getBbpsReqinfo().getHeaders().get(CoreContsants.REQ_TYPE));
        log.info("Fetching service class for RequestType [{}]", requestType);
        this.findImplProcess(requestType).process(coreProcessReqResp);
    }

    public CoreProcess findImplProcess(String requestType){
        return Optional.ofNullable(serviceImpl.get(requestType))
                .orElseThrow(() -> new IllegalArgumentException("Invalid request type"));
    }
}


