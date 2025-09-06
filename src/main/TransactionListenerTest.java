package com.jpmc.midascore;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;

import com.jpmc.midascore.foundation.Transaction;

@SpringBootTest
public class TransactionListenerTest {

    @Autowired
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @Test
    void sendFourTransactions() throws InterruptedException {
        Transaction t1 = new Transaction(1L, 101L, 100.0f);
        Transaction t2 = new Transaction(2L, 102L, 200.0f);
        Transaction t3 = new Transaction(3L, 103L, 300.0f);
        Transaction t4 = new Transaction(4L, 104L, 400.0f);


        kafkaTemplate.send("test-topic", t1);
        kafkaTemplate.send("test-topic", t2);
        kafkaTemplate.send("test-topic", t3);
        kafkaTemplate.send("test-topic", t4);

        // Give the listener a moment to process
        Thread.sleep(2000);
    }
}
