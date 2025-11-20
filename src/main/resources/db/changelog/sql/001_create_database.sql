--liquibase formatted sql
--changeset gift-api:1 create-child-and-gift-tables

-- Create table CHILD
CREATE TABLE child (
                       id BIGSERIAL PRIMARY KEY,
                       first_name VARCHAR(255) NOT NULL,
                       last_name VARCHAR(255) NOT NULL,
                       birth_date DATE NOT NULL
);

-- Create table GIFT
CREATE TABLE gift (
                      id BIGSERIAL PRIMARY KEY,
                      name VARCHAR(255) NOT NULL,
                      price NUMERIC(19,2) NOT NULL,
                      child_id BIGINT,
                      CONSTRAINT fk_gift_child FOREIGN KEY (child_id)
                          REFERENCES child(id)
                          ON DELETE CASCADE
);