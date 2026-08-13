CREATE SCHEMA IF NOT EXISTS cards_schema;

CREATE TABLE cards_schema.cards (
    card_id BIGSERIAL PRIMARY KEY,
    mobile_number VARCHAR(20) NOT NULL,
    card_number VARCHAR(20) NOT NULL,
    card_type VARCHAR(50) NOT NULL,
    total_limit INT NOT NULL,
    amount_used INT NOT NULL DEFAULT 0,
    available_amount INT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(50),
    updated_at TIMESTAMP,
    updated_by VARCHAR(50)
);

CREATE UNIQUE INDEX idx_cards_card_number ON cards_schema.cards(card_number);
CREATE INDEX idx_cards_mobile ON cards_schema.cards(mobile_number);
