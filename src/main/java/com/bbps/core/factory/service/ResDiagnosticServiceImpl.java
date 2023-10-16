package com.bbps.core.factory.service;

import com.bbps.core.factory.CoreProcess;
import com.bbps.core.kafka.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Service
@Slf4j
public class ResDiagnosticServiceImpl implements CoreProcess {

    @Override
    public void process(Message coreReqResp) {

    }
}
