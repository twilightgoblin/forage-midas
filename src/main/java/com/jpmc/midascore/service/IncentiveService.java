package com.jpmc.midascore.service;

import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class IncentiveService {
    
    private final RestTemplate restTemplate;
    private static final String INCENTIVE_API_URL = "http://localhost:8080/incentive";
    
    public IncentiveService() {
        this.restTemplate = new RestTemplate();
    }
    
    public Incentive getIncentive(Transaction transaction) {
        try {
            Incentive incentive = restTemplate.postForObject(INCENTIVE_API_URL, transaction, Incentive.class);
            return incentive != null ? incentive : new Incentive(0.0f);
        } catch (Exception e) {
            System.err.println("Error calling incentive API: " + e.getMessage());
            return new Incentive(0.0f);
        }
    }
}
