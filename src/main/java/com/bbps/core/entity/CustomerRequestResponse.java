package com.bbps.core.entity;


import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;

@Entity
@Data
@Table(name = "Customer_Request_Response",schema="bbps")
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "request_type")
    private String requestType;

    //BBPS txnId
    @Column(name = "ref_Id")
    private String refId;

    @Column(name = "agent_Id")
    private String agentId;

    @Column(name = "request")
    private String request;

    @Column(name = "request_timestamp")
    private Timestamp requestTimestamp;

    @Column(name = "response")
    private String response;

    @Column(name = "reponse_timestamp")
    private Timestamp responseTimestamp;

    @Column(name = "request_http_status")
    private String requestHttpStatus;

    @Column(name = "status")
    private String status;

}
