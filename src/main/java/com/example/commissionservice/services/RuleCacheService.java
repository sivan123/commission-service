package com.example.commissionservice.services;

import com.example.commissionservice.entity.CommissionRule;
import com.example.commissionservice.repository.CommissionRuleRepository;
import com.example.commissionservice.vos.Trade;
import one.util.streamex.MoreCollectors;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static com.example.commissionservice.utils.Constants.RuleLookUpFields.*;

@Service
public class RuleCacheService {
    Map<Long,Double> ruleCache = new HashMap<>(); //for now keep as a simple map of the rule id key and a computing factor to calculate the commission. This ideally would be some expression.
    Map<String, LinkedHashSet<Long>> counterPartyIdIndex = new HashMap<>();
    Map<String, LinkedHashSet<Long>> exchangeIndex= new HashMap<>();
    Map<String, LinkedHashSet<Long>> productIndex= new HashMap<>();
    Map<String, LinkedHashSet<Long>> tradeValueIndex = new HashMap<>();

    List<Long> sortedRuleIds;

    private final CommissionRuleRepository commissionRuleRepository;

    public RuleCacheService(CommissionRuleRepository commissionRuleRepository) {
        this.commissionRuleRepository = commissionRuleRepository;
    }

    public void loadCache()
    {
        List<CommissionRule> rules = commissionRuleRepository.findAllByOrderByRulePrecedence();
        rules.forEach(commissionRule -> ruleCache.put(commissionRule.getId(),commissionRule.getComputationFactor()));
        sortedRuleIds=rules.stream().map(CommissionRule::getRulePrecedence).collect(Collectors.toList());
        for (CommissionRule rule : rules) {
            counterPartyIdIndex.computeIfAbsent(rule.getCounterPartyId(), k -> new LinkedHashSet<>()).add(rule.getId());
        }
        for (CommissionRule rule : rules) {
            exchangeIndex.computeIfAbsent(rule.getExchange(), k -> new LinkedHashSet<>()).add(rule.getId());
        }
        for (CommissionRule rule : rules) {
            productIndex.computeIfAbsent(rule.getProduct(), k -> new LinkedHashSet<>()).add(rule.getId());
        }
        for (CommissionRule rule : rules) {
            tradeValueIndex.computeIfAbsent(rule.getTradeValue(), k -> new LinkedHashSet<>()).add(rule.getId());
        }

    }

    public Long getMatchingRule(Trade trade) throws Exception {

        String ctyId = trade.getFieldValue(CounterPartyId);
        String exch = trade.getFieldValue(Exchange);
        String tradeValue = trade.getFieldValue(TradeValue);
        String product = trade.getFieldValue(Product);

        LinkedHashSet<Long> matchingCptyRuleIds = Optional.ofNullable(counterPartyIdIndex.get(ctyId)).orElse(new LinkedHashSet<>());
        matchingCptyRuleIds.addAll(counterPartyIdIndex.get("*"));

        LinkedHashSet<Long> matchingExchangeRuleIds = Optional.ofNullable(exchangeIndex.get(exch)).orElse(new LinkedHashSet<>());;
        matchingExchangeRuleIds.addAll(exchangeIndex.get("*"));

        LinkedHashSet<Long> matchingTradeValueRuleIds = Optional.ofNullable(tradeValueIndex.get(tradeValue)).orElse(new LinkedHashSet<>());;
        matchingTradeValueRuleIds.addAll(tradeValueIndex.get("*"));

        LinkedHashSet<Long> matchingProductRuleIds = Optional.ofNullable(productIndex.get(product)).orElse(new LinkedHashSet<>());;
        matchingProductRuleIds.addAll(productIndex.get("*"));

        //this is just to get the smallest list so that the looping is minimal
        ArrayList<LinkedHashSet<Long>> filteredList = new ArrayList<>(); // just to find the smallest filtered one
        filteredList.add(matchingCptyRuleIds);
        filteredList.add(matchingExchangeRuleIds);
        filteredList.add(matchingTradeValueRuleIds);
        filteredList.add(matchingProductRuleIds);
        LinkedList<Long> minsizeList = new LinkedList(filteredList.stream().collect(MoreCollectors.minAll(Comparator.comparing(LinkedHashSet::size))).stream().collect(MoreCollectors.intersecting()));
        minsizeList.sort(Comparator.comparingInt(sortedRuleIds::indexOf));

        for (Long elem:minsizeList){
            if(matchingCptyRuleIds.contains(elem)&&matchingExchangeRuleIds.contains(elem)&&matchingTradeValueRuleIds.contains(elem)&&matchingProductRuleIds.contains(elem))
                return elem;
        }
        throw new Exception("No Matching rule found. Make sure that the catch all rule is set up");
    }



    public Map<Long, Double> getAllRules() {
        return ruleCache;
    }
     public List<Map<String, LinkedHashSet<Long>>> getIndexes()
     {
         return Arrays.asList(counterPartyIdIndex,exchangeIndex,productIndex,tradeValueIndex);
     }

    public Double getComputationalFactor(Long ruleId) {
        return ruleCache.get(ruleId);
    }
}
