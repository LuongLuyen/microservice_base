package com.eazybytes.cards.exception;

public class CardAlreadyExistsException extends com.eazybytes.common.exception.AlreadyExistsException {

    public CardAlreadyExistsException(String message) {
        super("Card", "cardNumber", message);
    }
}
