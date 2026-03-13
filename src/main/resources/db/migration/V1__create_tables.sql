CREATE TABLE status (
                        id BIGSERIAL PRIMARY KEY,
                        name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE subscription (
                              id VARCHAR(100) PRIMARY KEY,
                              status_id BIGINT NOT NULL,
                              created_at TIMESTAMP NOT NULL,
                              updated_at TIMESTAMP NOT NULL,

                              CONSTRAINT fk_subscription_status
                                  FOREIGN KEY (status_id)
                                      REFERENCES status(id)
);

CREATE TABLE event_history (
                               id BIGSERIAL PRIMARY KEY,
                               subscription_id VARCHAR(100) NOT NULL,
                               event_type VARCHAR(50) NOT NULL,
                               processed_at TIMESTAMP NOT NULL,

                               CONSTRAINT fk_event_subscription
                                   FOREIGN KEY (subscription_id)
                                       REFERENCES subscription(id)
);

INSERT INTO status (name) VALUES ('ACTIVE');
INSERT INTO status (name) VALUES ('CANCELED');