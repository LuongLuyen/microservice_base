package com.eazybytes.loans.controller;

import com.eazybytes.loans.dto.LoansContactInfoDto;
import com.eazybytes.loans.dto.LoansDto;
import com.eazybytes.loans.service.ILoansService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoansController.class)
class LoansControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ILoansService loansService;

    @MockitoBean
    private LoansContactInfoDto loansContactInfoDto;

    @Test
    void createLoan_shouldReturn201_whenValidInput() throws Exception {
        doNothing().when(loansService).createLoan(anyString());

        mockMvc.perform(post("/api/v1/create")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Loan created successfully"));
    }

    @Test
    void createLoan_shouldReturn400_whenInvalidMobileNumber() throws Exception {
        mockMvc.perform(post("/api/v1/create")
                        .param("mobileNumber", "123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchLoan_shouldReturn200_whenLoanExists() throws Exception {
        LoansDto loansDto = new LoansDto();
        loansDto.setMobileNumber("1234567890");
        loansDto.setLoanNumber("548732457654");
        loansDto.setLoanType("Home Loan");
        loansDto.setTotalLoan(100000);
        loansDto.setAmountPaid(0);
        loansDto.setOutstandingAmount(100000);

        when(loansService.fetchLoan(anyString())).thenReturn(loansDto);

        mockMvc.perform(get("/api/v1/fetch")
                        .header("eazybank-correlation-id", "test-correlation-id")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loanNumber").value("548732457654"))
                .andExpect(jsonPath("$.loanType").value("Home Loan"));
    }
}
