package com.jpmc.midascore.controller;

import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class BalanceController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/balance")
    public Balance getBalance(@RequestParam Long userId) {
        Optional<com.jpmc.midascore.entity.UserRecord> userRecord = userRepository.findById(userId);
        
        if (userRecord.isPresent()) {
            return new Balance(userRecord.get().getBalance());
        } else {
            return new Balance(0.0f);
        }
    }
}
