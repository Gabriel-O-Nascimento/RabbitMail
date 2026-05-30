CREATE DATABASE IF NOT EXISTS RabbitMailDB
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE RabbitMailDB;

CREATE TABLE IF NOT EXISTS recipients (
  id BIGINT NOT NULL AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  email VARCHAR(180) NOT NULL,
  active BOOLEAN NOT NULL DEFAULT TRUE,
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id),
  UNIQUE KEY uk_recipients_email (email),
  KEY idx_recipients_active (active),
  KEY idx_recipients_created_at (created_at)
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS email_messages (
  id BIGINT NOT NULL AUTO_INCREMENT,
  subject VARCHAR(180) NOT NULL,
  content LONGTEXT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',
  created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),

  PRIMARY KEY (id),
  KEY idx_email_messages_status (status),
  KEY idx_email_messages_created_at (created_at),
  CONSTRAINT chk_email_messages_status
    CHECK (status IN ('DRAFT', 'QUEUED', 'SENDING', 'SENT', 'ERROR', 'CANCELED'))
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS batch_sends (
  id BIGINT NOT NULL AUTO_INCREMENT,
  email_message_id BIGINT NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'REQUESTED',
  total_recipients INT NOT NULL DEFAULT 0,
  total_success INT NOT NULL DEFAULT 0,
  total_error INT NOT NULL DEFAULT 0,
  requested_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  processed_at DATETIME(6) NULL,

  PRIMARY KEY (id),
  KEY idx_batch_sends_email_message_id (email_message_id),
  KEY idx_batch_sends_status (status),
  KEY idx_batch_sends_requested_at (requested_at),
  CONSTRAINT fk_batch_sends_email_message
    FOREIGN KEY (email_message_id)
    REFERENCES email_messages (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT chk_batch_sends_status
    CHECK (status IN ('REQUESTED', 'PROCESSING', 'COMPLETED', 'COMPLETED_WITH_ERRORS', 'ERROR')),
  CONSTRAINT chk_batch_sends_totals
    CHECK (
      total_recipients >= 0
      AND total_success >= 0
      AND total_error >= 0
      AND total_success + total_error <= total_recipients
    )
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS batch_send_results (
  id BIGINT NOT NULL AUTO_INCREMENT,
  batch_send_id BIGINT NOT NULL,
  recipient_id BIGINT NOT NULL,
  email_used VARCHAR(180) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  error_message TEXT NULL,
  sent_at DATETIME(6) NULL,

  PRIMARY KEY (id),
  KEY idx_batch_send_results_batch_send_id (batch_send_id),
  KEY idx_batch_send_results_recipient_id (recipient_id),
  KEY idx_batch_send_results_status (status),
  KEY idx_batch_send_results_email_used (email_used),
  CONSTRAINT fk_batch_send_results_batch_send
    FOREIGN KEY (batch_send_id)
    REFERENCES batch_sends (id)
    ON UPDATE CASCADE
    ON DELETE CASCADE,
  CONSTRAINT fk_batch_send_results_recipient
    FOREIGN KEY (recipient_id)
    REFERENCES recipients (id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,
  CONSTRAINT chk_batch_send_results_status
    CHECK (status IN ('PENDING', 'SUCCESS', 'ERROR'))
) ENGINE=InnoDB;

INSERT INTO recipients (name, email, active) VALUES
  ('Ana Souza', 'ana.souza@example.com', TRUE),
  ('Bruno Lima', 'bruno.lima@example.com', TRUE),
  ('Carla Mendes', 'carla.mendes@example.com', TRUE),
  ('Diego Santos', 'diego.santos@example.com', FALSE);

INSERT INTO email_messages (subject, content, status) VALUES
  ('Bem-vindo ao RabbitMail', 'Ola! Esta e uma mensagem de teste enviada de forma assincrona.', 'DRAFT'),
  ('Comunicado academico', 'Este envio em lote sera processado por um Consumer RabbitMQ.', 'QUEUED');

INSERT INTO batch_sends (
  email_message_id,
  status,
  total_recipients,
  total_success,
  total_error,
  requested_at,
  processed_at
) VALUES
  (2, 'COMPLETED_WITH_ERRORS', 3, 2, 1, CURRENT_TIMESTAMP(6), CURRENT_TIMESTAMP(6));

INSERT INTO batch_send_results (
  batch_send_id,
  recipient_id,
  email_used,
  status,
  error_message,
  sent_at
) VALUES
  (1, 1, 'ana.souza@example.com', 'SUCCESS', NULL, CURRENT_TIMESTAMP(6)),
  (1, 2, 'bruno.lima@example.com', 'SUCCESS', NULL, CURRENT_TIMESTAMP(6)),
  (1, 3, 'carla.mendes@example.com', 'ERROR', 'Falha simulada no envio do e-mail.', CURRENT_TIMESTAMP(6));
