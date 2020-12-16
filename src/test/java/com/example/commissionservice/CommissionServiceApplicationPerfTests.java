package com.example.commissionservice;

import com.example.commissionservice.entity.CommissionRule;
import com.example.commissionservice.repository.CommissionRuleRepository;
import com.example.commissionservice.services.RuleCacheService;
import com.example.commissionservice.vos.CommissionResponse;
import com.example.commissionservice.vos.Trade;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CommissionServiceApplicationPerfTests {

    @LocalServerPort
    int randomServerPort;

    @Autowired
    CommissionRuleRepository ruleRepository;
    @Autowired
    RuleCacheService ruleCacheService;
    @BeforeAll
    public void loadData()
    {
        System.out.println("preloading data");
        for (int i = 0; i < 100000; i++) {
            CommissionRule rule = CommissionRule.builder()
                    .counterPartyId(RandomStringUtils.randomAlphanumeric(5))
                    .exchange(RandomStringUtils.randomAlphabetic(3))
                    .product(RandomStringUtils.randomAlphabetic(2))
                    .tradeValue(String.valueOf(RandomUtils.nextDouble()))
                    .rulePrecedence(i*11L)
                    .computationFactor(0.08)
                    .build();
            ruleRepository.save(rule);
        }
        //reload cache
        ruleCacheService.loadCache();
    }

    @Test
    public void testCalculateCommissionFallBackToCatchAllRule() throws URISyntaxException
    {

        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:"+randomServerPort+"/calculatedCommission";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        Trade trade = Trade.builder().product("ABCLL").exchange("XYZUUUU").tradeValue(1000.0).build();
        HttpEntity<Trade> request = new HttpEntity<>(trade, headers);


        ResponseEntity<CommissionResponse> result = restTemplate.postForEntity(uri, request, CommissionResponse.class);

        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(50.0,result.getBody().getCommission());
    }

}
