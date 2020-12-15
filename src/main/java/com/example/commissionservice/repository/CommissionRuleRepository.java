package com.example.commissionservice.repository;

import com.example.commissionservice.entity.CommissionRule;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommissionRuleRepository extends CrudRepository<CommissionRule, Long> {

    List<CommissionRule> findAllByOrderByRulePrecedence();
}
