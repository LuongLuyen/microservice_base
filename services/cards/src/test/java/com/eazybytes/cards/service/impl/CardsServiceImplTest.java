package com.eazybytes.cards.service.impl;

import com.eazybytes.cards.dto.CardsDto;
import com.eazybytes.cards.entity.Cards;
import com.eazybytes.cards.exception.CardAlreadyExistsException;
import com.eazybytes.cards.exception.ResourceNotFoundException;
import com.eazybytes.cards.repository.CardsRepository;
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
class CardsServiceImplTest {

    @Mock
    private CardsRepository cardsRepository;

    @InjectMocks
    private CardsServiceImpl cardsService;

    private Cards card;
    private CardsDto cardsDto;

    @BeforeEach
    void setUp() {
        card = new Cards();
        card.setCardId(1L);
        card.setMobileNumber("1234567890");
        card.setCardNumber("100646930341");
        card.setCardType("Credit Card");
        card.setTotalLimit(100000);
        card.setAmountUsed(0);
        card.setAvailableAmount(100000);

        cardsDto = new CardsDto();
        cardsDto.setMobileNumber("1234567890");
        cardsDto.setCardNumber("100646930341");
        cardsDto.setCardType("Credit Card");
        cardsDto.setTotalLimit(100000);
        cardsDto.setAmountUsed(0);
        cardsDto.setAvailableAmount(100000);
    }

    @Test
    void createCard_shouldSaveCard_whenCardDoesNotExist() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());
        when(cardsRepository.save(any(Cards.class))).thenReturn(card);

        cardsService.createCard("1234567890");

        verify(cardsRepository).save(any(Cards.class));
    }

    @Test
    void createCard_shouldThrowAlreadyExists_whenCardExists() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(card));

        assertThatThrownBy(() -> cardsService.createCard("1234567890"))
                .isInstanceOf(CardAlreadyExistsException.class);

        verify(cardsRepository, never()).save(any(Cards.class));
    }

    @Test
    void fetchCard_shouldReturnCardDto_whenCardExists() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(card));

        CardsDto result = cardsService.fetchCard("1234567890");

        assertThat(result).isNotNull();
        assertThat(result.getCardNumber()).isEqualTo("100646930341");
        assertThat(result.getCardType()).isEqualTo("Credit Card");
        assertThat(result.getMobileNumber()).isEqualTo("1234567890");
    }

    @Test
    void fetchCard_shouldThrowResourceNotFound_whenCardDoesNotExist() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardsService.fetchCard("1234567890"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void updateCard_shouldReturnTrue_whenCardExists() {
        when(cardsRepository.findByCardNumber(anyString())).thenReturn(Optional.of(card));
        when(cardsRepository.save(any(Cards.class))).thenReturn(card);

        boolean result = cardsService.updateCard(cardsDto);

        assertThat(result).isTrue();
        verify(cardsRepository).save(any(Cards.class));
    }

    @Test
    void updateCard_shouldThrowResourceNotFound_whenCardDoesNotExist() {
        when(cardsRepository.findByCardNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardsService.updateCard(cardsDto))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteCard_shouldReturnTrue_whenCardExists() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.of(card));
        doNothing().when(cardsRepository).deleteById(any(Long.class));

        boolean result = cardsService.deleteCard("1234567890");

        assertThat(result).isTrue();
        verify(cardsRepository).deleteById(1L);
    }

    @Test
    void deleteCard_shouldThrowResourceNotFound_whenCardDoesNotExist() {
        when(cardsRepository.findByMobileNumber(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cardsService.deleteCard("1234567890"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
