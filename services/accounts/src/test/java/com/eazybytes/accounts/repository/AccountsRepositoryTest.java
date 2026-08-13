package com.eazybytes.accounts.repository;

import com.eazybytes.accounts.entity.Accounts;
import com.eazybytes.accounts.entity.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers(disabledWithoutDocker = true)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AccountsRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.flyway.enabled", () -> "false");
    }

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private AccountsRepository accountsRepository;

    @Test
    void shouldSaveAndFindCustomerByMobileNumber() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setMobileNumber("1234567890");

        Customer savedCustomer = customerRepository.save(customer);

        Optional<Customer> found = customerRepository.findByMobileNumber("1234567890");

        assertThat(found).isPresent();
        assertThat(found.get().getCustomerId()).isEqualTo(savedCustomer.getCustomerId());
        assertThat(found.get().getName()).isEqualTo("Test Customer");
    }

    @Test
    void shouldSaveAndFindAccountByCustomerId() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setEmail("test@example.com");
        customer.setMobileNumber("1234567890");
        Customer savedCustomer = customerRepository.save(customer);

        Accounts account = new Accounts();
        account.setCustomerId(savedCustomer.getCustomerId());
        account.setAccountNumber(1000000000L);
        account.setAccountType("Savings");
        account.setBranchAddress("123 Main Street");
        account.setCommunicationSw(false);
        accountsRepository.save(account);

        Optional<Accounts> found = accountsRepository.findByCustomerId(savedCustomer.getCustomerId());

        assertThat(found).isPresent();
        assertThat(found.get().getAccountNumber()).isEqualTo(1000000000L);
        assertThat(found.get().getAccountType()).isEqualTo("Savings");
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        Optional<Customer> found = customerRepository.findByMobileNumber("9999999999");

        assertThat(found).isEmpty();
    }
}
