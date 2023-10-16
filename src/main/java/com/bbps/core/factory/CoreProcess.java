package com.bbps.core.factory;

import com.bbps.core.kafka.model.Message;

public interface CoreProcess {
    public void process(Message coreReqResp);
}
