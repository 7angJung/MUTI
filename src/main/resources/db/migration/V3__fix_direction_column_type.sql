-- V3: Fix column type for direction in question_options
-- Change VARCHAR(1) to CHAR(1) for Hibernate compatibility

ALTER TABLE question_options
ALTER COLUMN direction TYPE CHAR(1);