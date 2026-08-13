package com.eazybytes.cards.controller;

import com.eazybytes.cards.dto.CardsContactInfoDto;
import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.service.ICardsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CardsController.class)
class CardsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ICardsService cardsService;

    @MockitoBean
    private CardsContactInfoDto cardsContactInfoDto;

    @Test
    void createCard_shouldReturn201_whenValidInput() throws Exception {
        doNothing().when(cardsService).createCard(anyString());

        mockMvc.perform(post("/api/v1/create")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statusCode").value("201"))
                .andExpect(jsonPath("$.statusMsg").value("Card created successfully"));
    }

    @Test
    void createCard_shouldReturn400_whenInvalidMobileNumber() throws Exception {
        mockMvc.perform(post("/api/v1/create")
                        .param("mobileNumber", "123"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchCard_shouldReturn200_whenCardExists() throws Exception {
        CardsDto cardsDto = new CardsDto();
        cardsDto.setMobileNumber("1234567890");
        cardsDto.setCardNumber("100646930341");
        cardsDto.setCardType("Credit Card");
        cardsDto.setTotalLimit(100000);
        cardsDto.setAmountUsed(0);
        cardsDto.setAvailableAmount(100000);

        when(cardsService.fetchCard(anyString())).thenReturn(cardsDto);

        mockMvc.perform(get("/api/v1/fetch")
                        .header("eazybank-correlation-id", "test-correlation-id")
                        .param("mobileNumber", "1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cardNumber").value("100646930341"))
                .andExpect(jsonPath("$.cardType").value("Credit Card"));
    }
}
