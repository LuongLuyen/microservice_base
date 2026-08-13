package com.eazybytes.accounts.service.impl;

import com.eazybytes.accounts.dto.AccountsDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import com.eazybytes.accounts.exception.CustomerAlreadyExistsException;
import com.eazybytes.accounts.exception.ResourceNotFoundException;
import com.eazybytes.accounts.repository.AccountsRepository;
import com.eazybytes.accounts.repository.CustomerRepository;
import com.eazybytes.accounts.service.IOutboxService;
import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceImplTest {

    @Mock
    private AccountsRepository accountsRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private IOutboxService outboxService;

    @Mock
    private Counter accountsCreatedCounter;

    @Mock
    private Counter accountsFetchedCounter;

    @InjectMocks
    private AccountsServiceImpl accountsService;

    private CustomerDto customerDto;
    private Customer customer;
    private Accounts account;

    @BeforeEach
    void setUp() {
        customerDto = new CustomerDto();
        customerDto.setName("Eazy Bytes");
        customerDto.setEmail("tutor@eazybytes.com");
        customerDto.setMobileNumber("1234567890");

        customer = new Customer();
        customer.setCustomerId(1L);
        customer.setName("Eazy Bytes");
        customer.setEmail("tutor@eazybytes.com");
        customer.setMobileNumber("1234567890");

        account = new Accounts();
        account.setAccountNumber(1000000000L);
        account.setCustomerId(1L);
        account.setAccountType("Savings");
        account.setBranchAddress("123 Main Street");
        account.setCommunicationSw(false);
    }

    @Test
    void createAccount_shouldSaveCustomerAndAccount_whenCustomerDoesNotExist() {
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(accountsRepository.save(any(Accounts.class))).thenReturn(account);

        accountsService.createAccount(customerDto);

        verify(customerRepository).save(any(Customer.class));
        verify(accountsRepository).save(any(Accounts.class));
        verify(outboxService).saveAccountCreatedEvent(any(Customer.class), any(Long.class));
    }

    @Test
    void createAccount_shouldThrowAlreadyExistsException_whenCustomerExists() {
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> accountsService.createAccount(customerDto))
                .isInstanceOf(CustomerAlreadyExistsException.class);

        verify(customerRepository, never()).save(any(Customer.class));
        verify(accountsRepository, never()).save(any(Accounts.class));
    }

    @Test
    void fetchAccount_shouldReturnCustomerDto_whenCustomerAndAccountExist() {
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));
        when(accountsRepository.findByCustomerId(any(Long.class))).thenReturn(Optional.of(account));

        CustomerDto result = accountsService.fetchAccount("1234567890");

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Eazy Bytes");
        assertThat(result.getEmail()).isEqualTo("tutor@eazybytes.com");
        assertThat(result.getMobileNumber()).isEqualTo("1234567890");
        assertThat(result.getAccountsDto()).isNotNull();
        assertThat(result.getAccountsDto().getAccountNumber()).isEqualTo(1000000000L);
    }

    @Test
    void fetchAccount_shouldThrowResourceNotFound_whenCustomerDoesNotExist() {
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountsService.fetchAccount("1234567890"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateAccount_shouldReturnTrue_whenAccountExists() {
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(1000000000L);
        accountsDto.setAccountType("Current");
        accountsDto.setBranchAddress("456 Main Street");
        customerDto.setAccountsDto(accountsDto);

        when(accountsRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
        when(accountsRepository.save(any(Accounts.class))).thenReturn(account);
        when(customerRepository.findById(any(Long.class))).thenReturn(Optional.of(customer));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        boolean result = accountsService.updateAccount(customerDto);

        assertThat(result).isTrue();
        verify(accountsRepository).save(any(Accounts.class));
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void updateAccount_shouldThrowResourceNotFound_whenAccountDoesNotExist() {
        AccountsDto accountsDto = new AccountsDto();
        accountsDto.setAccountNumber(9999999999L);
        customerDto.setAccountsDto(accountsDto);

        when(accountsRepository.findById(any(Long.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> accountsService.updateAccount(customerDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteAccount_shouldReturnTrue_whenCustomerExists() {
        when(customerRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(customer));
        doNothing().when(accountsRepository).deleteByCustomerId(any(Long.class));
        doNothing().when(customerRepository).deleteById(any(Long.class));

        boolean result = accountsService.deleteAccount("1234567890");

        assertThat(result).isTrue();
        verify(accountsRepository).deleteByCustomerId(1L);
        verify(customerRepository).deleteById(1L);
    }

    @Test
    void updateCommunicationStatus_shouldReturnTrue_whenAccountExists() {
        when(accountsRepository.findById(any(Long.class))).thenReturn(Optional.of(account));
        when(accountsRepository.save(any(Accounts.class))).thenReturn(account);

        boolean result = accountsService.updateCommunicationStatus(1000000000L);

        assertThat(result).isTrue();
        verify(accountsRepository).save(account);
    }
}
