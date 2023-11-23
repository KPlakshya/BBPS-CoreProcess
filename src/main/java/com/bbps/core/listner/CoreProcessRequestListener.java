package com.bbps.core.listner;

import com.bbps.core.constants.APIMappingConstant;
import com.bbps.core.kafka.model.Message;
import com.bbps.core.service.CoreProcessService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class CoreProcessRequestListener {

//    @Value("${customer.posting.request}")
//    String corProcessReqTopic;

    @Autowired
    CoreProcessService coreProcessService;

    @KafkaListener(topics = "BillFetchResponse",groupId = "bbps_core_reqresp1")
    public void getPostingRequest(String kafkaReqData, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            log.info("Message received to process posting reports Queue [{}] Message", topic, kafkaReqData);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Message message = mapper.readValue(kafkaReqData, Message.class);
            coreProcessService.findReqType(message);
        } catch (JsonProcessingException jpe) {
            log.error("Topic [{}] ,JsonProcessing Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, jpe);
        } catch (Exception e) {
            log.error("Topic [{}] ,Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, e);
        }
    }
    @KafkaListener(topics = "BillerFetchResponse",groupId = "bbps_core_reqresp1")
    public void billerFetch(String kafkaReqData, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            log.info("Message received to process posting reports Queue [{}] Message", topic, kafkaReqData);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Message message = mapper.readValue(kafkaReqData, Message.class);
            coreProcessService.findReqType(message);
        } catch (JsonProcessingException jpe) {
            log.error("Topic [{}] ,JsonProcessing Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, jpe);
        } catch (Exception e) {
            log.error("Topic [{}] ,Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, e);
        }
    }

    @KafkaListener(topics = "BillPaymentResponse",groupId = "BillPaymentResponse_group")
    public void billPayment(String kafkaReqData, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            log.info("Message received to process posting reports Queue [{}] Message", topic, kafkaReqData);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Message message = mapper.readValue(kafkaReqData, Message.class);
            coreProcessService.findReqType(message);
        } catch (JsonProcessingException jpe) {
            log.error("Topic [{}] ,JsonProcessing Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, jpe);
        } catch (Exception e) {
            log.error("Topic [{}] ,Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, e);
        }
    }

    @KafkaListener(topics = "BillValidationResponse",groupId = "BillValidationResponse_group")
    public void billValidation(String kafkaReqData, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        try {
            log.info("Message received to process posting reports Queue [{}] Message", topic, kafkaReqData);
            ObjectMapper mapper = new ObjectMapper()
                    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            Message message = mapper.readValue(kafkaReqData, Message.class);
            coreProcessService.findReqType(message);
        } catch (JsonProcessingException jpe) {
            log.error("Topic [{}] ,JsonProcessing Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, jpe);
        } catch (Exception e) {
            log.error("Topic [{}] ,Exception While Receiving Request [{}] ,Exception [{}]", topic, kafkaReqData, e);
        }
    }

}
