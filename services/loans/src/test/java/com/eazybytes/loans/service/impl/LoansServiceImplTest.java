package com.eazybytes.loans.service.impl;

import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.entity.Loans;
import com.eazybytes.loans.exception.LoanAlreadyExistsException;
import com.eazybytes.loans.exception.ResourceNotFoundException;
import com.eazybytes.loans.repository.LoansRepository;
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
class LoansServiceImplTest {

    @Mock
    private LoansRepository loansRepository;

    @InjectMocks
    private LoansServiceImpl loansService;

    private Loans loan;
    private LoansDto loansDto;

    @BeforeEach
    void setUp() {
        loan = new Loans();
        loan.setLoanId(1L);
        loan.setMobileNumber("1234567890");
        loan.setLoanNumber("548732457654");
        loan.setLoanType("Home Loan");
        loan.setTotalLoan(100000);
        loan.setAmountPaid(0);
        loan.setOutstandingAmount(100000);

        loansDto = new LoansDto();
        loansDto.setMobileNumber("1234567890");
        loansDto.setLoanNumber("548732457654");
        loansDto.setLoanType("Home Loan");
        loansDto.setTotalLoan(100000);
        loansDto.setAmountPaid(0);
        loansDto.setOutstandingAmount(100000);
    }

    @Test
    void createLoan_shouldSaveLoan_whenLoanDoesNotExist() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(loansRepository.save(any(Loans.class))).thenReturn(loan);

        loansService.createLoan("1234567890");

        verify(loansRepository).save(any(Loans.class));
    }

    @Test
    void createLoan_shouldThrowAlreadyExists_whenLoanExists() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(loan));

        assertThatThrownBy(() -> loansService.createLoan("1234567890"))
                .isInstanceOf(LoanAlreadyExistsException.class);

        verify(loansRepository, never()).save(any(Loans.class));
    }

    @Test
    void fetchLoan_shouldReturnLoanDto_whenLoanExists() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(loan));

        LoansDto result = loansService.fetchLoan("1234567890");

        assertThat(result).isNotNull();
        assertThat(result.getLoanNumber()).isEqualTo("548732457654");
        assertThat(result.getLoanType()).isEqualTo("Home Loan");
        assertThat(result.getMobileNumber()).isEqualTo("1234567890");
    }

    @Test
    void fetchLoan_shouldThrowResourceNotFound_whenLoanDoesNotExist() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.fetchLoan("1234567890"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateLoan_shouldReturnTrue_whenLoanExists() {
        when(loansRepository.findByLoanNumber(anyString())).thenReturn(Optional.of(loan));
        when(loansRepository.save(any(Loans.class))).thenReturn(loan);

        boolean result = loansService.updateLoan(loansDto);

        assertThat(result).isTrue();
        verify(loansRepository).save(any(Loans.class));
    }

    @Test
    void updateLoan_shouldThrowResourceNotFound_whenLoanDoesNotExist() {
        when(loansRepository.findByLoanNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.updateLoan(loansDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteLoan_shouldReturnTrue_whenLoanExists() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(loan));
        doNothing().when(loansRepository).deleteById(any(Long.class));

        boolean result = loansService.deleteLoan("1234567890");

        assertThat(result).isTrue();
        verify(loansRepository).deleteById(1L);
    }

    @Test
    void deleteLoan_shouldThrowResourceNotFound_whenLoanDoesNotExist() {
        when(loansRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> loansService.deleteLoan("1234567890"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
