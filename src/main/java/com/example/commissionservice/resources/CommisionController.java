package com.example.commissionservice.resources;

import com.example.commissionservice.services.CommissionCalculationService;
import com.example.commissionservice.services.RuleCacheService;
import com.example.commissionservice.vos.CommissionResponse;
import com.example.commissionservice.vos.Trade;
import lombok.extern.java.Log;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Log
@RestController("/commissions/")
public class CommisionController {


    private  final CommissionCalculationService commissionCalculationService;
    private final RuleCacheService ruleCacheService;

    public CommisionController(CommissionCalculationService commissionCalculationService, RuleCacheService ruleCacheService) {
        this.commissionCalculationService = commissionCalculationService;
        this.ruleCacheService = ruleCacheService;
    }

    @PostMapping("calculatedCommission")
    public CommissionResponse getCalculatedCommision(@RequestBody Trade trade)
    {
        try {
            return commissionCalculationService.calculateCommission(trade);
        } catch (Exception e) {
            log.log(Level.SEVERE,e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,"Unable to calculate commission");
        }
    }

    @GetMapping("rules")
    public Map<Long, Double> getRules()
    {
        return ruleCacheService.getAllRules();
    }

    @GetMapping("indexes")
    public List<Map<String, LinkedHashSet<Long>>> getIndexes()
    {
        return ruleCacheService.getIndexes();
    }

}
