package com.eazybytes.accounts.controller;

import com.eazybytes.accounts.dto.AccountsContactInfoDto;
import com.eazybytes.accounts.dto.CustomerDto;
import com.eazybytes.accounts.service.IAccountsService;
import com.eazybytes.common.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountsController.class)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IAccountsService accountsService;

    @MockitoBean
    private AccountsContactInfoDto accountsContactInfoDto;

    @Test
    void createAccount_shouldReturn201_whenValidInput() throws Exception {
        doNothing().when(accountsService).createAccount(any(CustomerDto.class));

        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Eazy Bytes");
        customerDto.setEmail("tutor@eazybytes.com");
        customerDto.setMobileNumber("1234567890");

        mockMvc.perform(post("/api/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Account created successfully"));
    }

    @Test
    void createAccount_shouldReturn400_whenValidationFails() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Eazy");
        customerDto.setEmail("invalid-email");
        customerDto.setMobileNumber("123");

        mockMvc.perform(post("/api/v1/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchAccount_shouldReturn200_whenAccountExists() throws Exception {
        CustomerDto customerDto = new CustomerDto();
        customerDto.setName("Eazy Bytes");
        customerDto.setEmail("tutor@eazybytes.com");
        customerDto.setMobileNumber("1234567890");

        when(accountsService.fetchAccount("1234567890")).thenReturn(customerDto);

        mockMvc.perform(get("/api/v1/fetch")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Eazy Bytes"))
                .andExpect(jsonPath("$.mobileNumber").value("1234567890"));
    }

    @Test
    void fetchAccount_shouldReturn400_whenMobileNumberInvalid() throws Exception {
        mockMvc.perform(get("/api/v1/fetch")
                        .param("mobileNumber", "123"))
                .andExpect(status().isBadRequest());
    }
}
