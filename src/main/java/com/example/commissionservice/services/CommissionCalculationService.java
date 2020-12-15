package com.example.commissionservice.services;

import com.example.commissionservice.vos.CommissionResponse;
import com.example.commissionservice.vos.Trade;
import org.springframework.stereotype.Service;

@Service
public class CommissionCalculationService {

    private final RuleCacheService ruleCacheService;

    public CommissionCalculationService(RuleCacheService ruleCacheService) {
        this.ruleCacheService = ruleCacheService;
    }

    public CommissionResponse calculateCommission(Trade trade) throws Exception {
        CommissionResponse commissionResponse = new CommissionResponse();
        Long ruleId = ruleCacheService.getMatchingRule(trade);
        Double compFactor = ruleCacheService.getComputationalFactor(ruleId);
        commissionResponse.setTradeId(trade.getTradeId());
        //sample implementation. Ideally would store an expression and evaluate it as the computation would likely be a complex formula taking additional attributes
        commissionResponse.setCommission(trade.getTradeValue()==null?1000:trade.getTradeValue()*compFactor);
        return commissionResponse;
    }


}
