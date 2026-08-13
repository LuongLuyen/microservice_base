package com.eazybytes.accounts.service;

import com.eazybytes.accounts.entity.Customer;

public interface IOutboxService {

    void saveAccountCreatedEvent(Customer customer, Long accountNumber);

    void publishPendingEvents();
}
