package com.example.account.service;

import com.example.account.entity.Account;
import com.example.account.repository.AccountRepository;
import com.example.common.kafka.events.*;
import com.example.common.kafka.topics.KafkaTopics;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountService {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final AccountRepository accountRepository;

    public AccountService(KafkaTemplate<String, Object> kafkaTemplate,
                          AccountRepository accountRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.accountRepository = accountRepository;
    }

    @Transactional
    public String openAccount(String userId) {
        Account account = new Account();
        account.setUserId(userId);
        account.setStatus("PENDING_DOCS");
        accountRepository.save(account);

        String accountId = account.getId().toString();

        kafkaTemplate.send(KafkaTopics.ACCOUNT_CREATED,
                new AccountCreatedEvent(accountId, userId));

        return accountId;
    }

    // Compensation handler for saga failure
    @KafkaListener(topics = KafkaTopics.DOCUMENT_FAILED, groupId = "account-service")
    @Transactional
    public void onDocumentFailed(DocumentFailedEvent event) {
        Long accId = Long.valueOf(event.accountId());
        accountRepository.findById(accId).ifPresent(acc -> {
            acc.setStatus("CANCELLED");
            accountRepository.save(acc);
        });

        kafkaTemplate.send(KafkaTopics.ACCOUNT_CANCELLED,
                new AccountCancelledEvent(event.accountId(), event.reason()));
    }
}
