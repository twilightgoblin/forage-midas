package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.TransactionRepository;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DatabaseConduit {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public DatabaseConduit(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
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

        // Process the transaction
        sender.setBalance(sender.getBalance() - amount);
        recipient.setBalance(recipient.getBalance() + amount);

        // Save updated user balances
        userRepository.save(sender);
        userRepository.save(recipient);

        // Create and save transaction record
        TransactionRecord transactionRecord = new TransactionRecord(sender, recipient, amount);
        transactionRepository.save(transactionRecord);

        System.out.println("Transaction processed: " + sender.getName() + " -> " + recipient.getName() + " $" + amount);
        return true;
    }
}
