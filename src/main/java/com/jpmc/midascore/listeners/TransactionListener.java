package com.jpmc.midascore.listeners;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.foundation.Transaction;

@Component
public class TransactionListener {

    private final List<Float> firstFourAmounts = new ArrayList<>();
    private final DatabaseConduit databaseConduit;

    @Autowired
    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core-group")
    public void listen(Transaction transaction) {
        System.out.println("Received transaction: " + transaction);

        // Collect first four transaction amounts
        if (firstFourAmounts.size() < 4) {
            firstFourAmounts.add(transaction.getAmount());

            // Print them once we have all four
            if (firstFourAmounts.size() == 4) {
                System.out.println("First four transaction amounts: " + firstFourAmounts);
            }
        }

        // Process the transaction through the database
        boolean success = databaseConduit.processTransaction(
            transaction.getSenderId(),
            transaction.getRecipientId(),
            transaction.getAmount()
        );

        if (!success) {
            System.out.println("Transaction discarded: " + transaction);
        }
    }
}


