package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class BalanceChecker implements CommandLineRunner {

    @Autowired
    private DatabaseConduit databaseConduit;

    @Override
    public void run(String... args) throws Exception {
        // Find wilbur by name (assuming wilbur has ID 9 based on the test output)
        Optional<UserRecord> wilburOpt = databaseConduit.findUserById(9);
        if (wilburOpt.isPresent()) {
            UserRecord wilbur = wilburOpt.get();
            System.out.println("Wilbur's current balance: " + wilbur.getBalance());
        } else {
            System.out.println("Wilbur not found");
        }
    }
}
