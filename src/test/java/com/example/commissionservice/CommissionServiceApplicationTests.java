package com.example.commissionservice;

import com.example.commissionservice.vos.CommissionResponse;
import com.example.commissionservice.vos.Trade;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CommissionServiceApplicationTests {

    @LocalServerPort
    int randomServerPort;

    @Test
    public void testCalculateCommissionSpecificMatch() throws URISyntaxException
    {
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:"+randomServerPort+"/calculatedCommission";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        Trade trade = Trade.builder().counterParty("1").product("FX").exchange("NYC").tradeValue(1000.0).build();
        HttpEntity<Trade> request = new HttpEntity<>(trade, headers);
        ResponseEntity<CommissionResponse> result = restTemplate.postForEntity(uri, request, CommissionResponse.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(20.0,result.getBody().getCommission());

    }

    @Test
    public void testCalculateCommissionSpecificMatchBlankValue() throws URISyntaxException
    {
        //counterparty not populated
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:"+randomServerPort+"/calculatedCommission";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        Trade trade = Trade.builder().product("FX").exchange("LON").tradeValue(1000.0).build();
        HttpEntity<Trade> request = new HttpEntity<>(trade, headers);
        ResponseEntity<CommissionResponse> result = restTemplate.postForEntity(uri, request, CommissionResponse.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(30.0,result.getBody().getCommission());
    }

    @Test
    public void testCalculateCommissionFallBackToGenericRule() throws URISyntaxException
    {
        //product not available in the cache
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:"+randomServerPort+"/calculatedCommission";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        Trade trade = Trade.builder().product("TWD").exchange("NYC").tradeValue(1000.0).build();
        HttpEntity<Trade> request = new HttpEntity<>(trade, headers);
        ResponseEntity<CommissionResponse> result = restTemplate.postForEntity(uri, request, CommissionResponse.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(70.0,result.getBody().getCommission());
    }

    @Test
    public void testCalculateCommissionFallBackToCatchAllRule() throws URISyntaxException
    {
        //product and exchange not available in the cache
        RestTemplate restTemplate = new RestTemplate();
        final String baseUrl = "http://localhost:"+randomServerPort+"/calculatedCommission";
        URI uri = new URI(baseUrl);
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-COM-PERSIST", "true");
        Trade trade = Trade.builder().product("ABC").exchange("XYZ").tradeValue(1000.0).build();
        HttpEntity<Trade> request = new HttpEntity<>(trade, headers);
        ResponseEntity<CommissionResponse> result = restTemplate.postForEntity(uri, request, CommissionResponse.class);
        Assertions.assertEquals(200, result.getStatusCodeValue());
        Assertions.assertEquals(50.0,result.getBody().getCommission());
    }

}
