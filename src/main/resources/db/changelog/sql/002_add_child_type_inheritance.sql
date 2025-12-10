--liquibase formatted sql
--changeset gift-api:2 add-child-type-and-specific-attributes

-- Add discriminator column for child type
ALTER TABLE child ADD COLUMN child_type VARCHAR(50) NOT NULL DEFAULT 'CHILD';

-- Add Boy specific attributes
ALTER TABLE child ADD COLUMN favorite_sport VARCHAR(255);

-- Add Girl specific attributes
ALTER TABLE child ADD COLUMN dress_color VARCHAR(100);

