package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.AccountsMsgDto;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.entity.OutboxEvent;
import com.eazybytes.accounts.repository.OutboxRepository;
import com.eazybytes.accounts.service.IOutboxService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class OutboxServiceImpl implements IOutboxService {

    private static final Logger log = LoggerFactory.getLogger(OutboxServiceImpl.class);

    private final OutboxRepository outboxRepository;
    private final StreamBridge streamBridge;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public void saveAccountCreatedEvent(Customer customer, Long accountNumber) {
        try {
            AccountsMsgDto msg = new AccountsMsgDto(
                    accountNumber,
                    customer.getName(),
                    customer.getEmail(),
                    customer.getMobileNumber()
            );
            OutboxEvent event = new OutboxEvent();
            event.setAggregateType("Customer");
            event.setAggregateId(customer.getCustomerId().toString());
            event.setEventType("AccountCreated");
            event.setPayload(objectMapper.writeValueAsString(msg));
            event.setCreatedAt(LocalDateTime.now());
            event.setPublished(false);
            outboxRepository.save(event);
        } catch (Exception e) {
            log.error("Failed to save outbox event", e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }

    @Override
    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxRepository.findByPublishedFalseOrderByCreatedAtAsc();
        for (OutboxEvent event : pendingEvents) {
            try {
                boolean sent = streamBridge.send("sendCommunication-out-0", event.getPayload());
                if (sent) {
                    event.setPublished(true);
                    event.setPublishedAt(LocalDateTime.now());
                    outboxRepository.save(event);
                    log.info("Published outbox event: {}", event.getId());
                }
            } catch (Exception e) {
                log.error("Failed to publish outbox event: {}", event.getId(), e);
            }
        }
    }
}
