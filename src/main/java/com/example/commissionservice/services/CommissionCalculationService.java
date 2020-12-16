package com.example.commissionservice.services;

import com.example.commissionservice.vos.CommissionResponse;
import com.example.commissionservice.vos.Trade;
import lombok.extern.java.Log;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.logging.Level;

@Service
@Log
public class CommissionCalculationService {

    private final RuleCacheService ruleCacheService;

    public CommissionCalculationService(RuleCacheService ruleCacheService) {
        this.ruleCacheService = ruleCacheService;
    }

    public CommissionResponse calculateCommission(Trade trade) throws Exception {
        CommissionResponse commissionResponse = new CommissionResponse();
        //long startTime = System.nanoTime();
        Long ruleId = ruleCacheService.getMatchingRule(trade);
        //long endTime = System.nanoTime();
        //log.log(Level.INFO,"Rule id fetch time "+((endTime - startTime)/1000000));

        //long startTime2 = System.nanoTime();
        Double compFactor = ruleCacheService.getComputationalFactor(ruleId);
        //long endTime2 = System.nanoTime();
        //log.log(Level.INFO,"Computation fetch time "+((endTime2 - startTime2)/1000000));

        commissionResponse.setTradeId(trade.getTradeId());
        //sample implementation. Ideally would store an expression and evaluate it as the computation would likely be a complex formula taking additional attributes
        commissionResponse.setCommission(trade.getTradeValue()==null?1000:trade.getTradeValue()*compFactor);
        return commissionResponse;
    }


}
