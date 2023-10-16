package com.bbps.core.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value="/bbps/customerapp/")
public class CustomerToBBPSController {

//    @Autowired
//    CoreProcessService billerService;
//
//    @PostMapping(value = APIMappingConstant.BILLER_FETCH_REQUEST,produces = MediaType.APPLICATION_JSON_VALUE,consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<Object> getBillerFetchRequest(@RequestBody BillerFetchRequest billerFetchRequest) {
//        log.info("BILLER - Fetch Request Received [{}]",billerFetchRequest);
//       Object response= billerService.processRequest(billerFetchRequest,APIMappingConstant.BILLER_FETCH_REQUEST);
//    return ResponseEntity.status(HttpStatus.OK).body(response);
//    }


}
