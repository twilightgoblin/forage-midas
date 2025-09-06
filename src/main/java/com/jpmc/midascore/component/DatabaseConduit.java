package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Incentive;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import com.jpmc.midascore.service.IncentiveService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final IncentiveService incentiveService;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository, IncentiveService incentiveService) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.incentiveService = incentiveService;
    }

    public void save(UserRecord userRecord) {
        userRepository.save(userRecord);
    }

    public Optional<UserRecord> findUserById(long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public boolean processTransaction(long senderId, long recipientId, float amount) {
        // Find sender and recipient
        Optional<UserRecord> senderOpt = userRepository.findById(senderId);
        Optional<UserRecord> recipientOpt = userRepository.findById(recipientId);

        // Validate that both users exist
        if (senderOpt.isEmpty() || recipientOpt.isEmpty()) {
            System.out.println("Transaction rejected: Invalid sender or recipient ID");
            return false;
        }

        UserRecord sender = senderOpt.get();
        UserRecord recipient = recipientOpt.get();

        // Validate that sender has sufficient balance
        if (sender.getBalance() < amount) {
            System.out.println("Transaction rejected: Insufficient balance for sender " + sender.getName());
            return false;
        }

        // Create Transaction object for incentive API call
        Transaction transaction = new Transaction(senderId, recipientId, amount);
        
        // Get incentive from the incentive API
        Incentive incentive = incentiveService.getIncentive(transaction);
        float incentiveAmount = incentive.getAmount();

        // Process the transaction
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount + incentiveAmount);

        // Save updated user balances
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record with incentive
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount, incentiveAmount);
        transactionRepository.save(transactionRecord);

        System.out.println("Transaction processed: " + sender.getName() + " -> " + recipient.getName() + " $" + amount + " (incentive: $" + incentiveAmount + ")");
        return true;
    }
}
