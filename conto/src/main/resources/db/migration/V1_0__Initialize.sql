
CREATE TABLE user (
  username VARCHAR(20) NOT NULL PRIMARY KEY,
  canonical_username VARCHAR(20) NOT NULL UNIQUE,
  role VARCHAR(10) NOT NULL,
  password varchar(61) NOT NULL
);

-- To mitigate the problem of guessable account IDs, we generate them
-- using a sequence with random values for start and increment
CREATE SEQUENCE seq_acc_id
START WITH ( SELECT round(1000000 * rand()))
INCREMENT BY ( SELECT 1 + round(1000 * rand()));

CREATE TABLE account (
  account_id      VARCHAR(40) NOT NULL PRIMARY KEY,
  owner           VARCHAR_IGNORECASE(50),
  description     VARCHAR(64) NOT NULL,
  minimum_balance BIGINT      NOT NULL,
  FOREIGN KEY (owner) REFERENCES user (username)
);

CREATE TABLE transfer (
  transfer_id       BIGINT       NOT NULL PRIMARY KEY AUTO_INCREMENT,
  debit_account_id  VARCHAR(40)  NOT NULL,
  credit_account_id VARCHAR(40)  NOT NULL,
  amount            BIGINT       NOT NULL,
  description       VARCHAR(512) NOT NULL,
  FOREIGN KEY (debit_account_id) REFERENCES account (account_id),
  FOREIGN KEY (credit_account_id) REFERENCES account (account_id)
);
