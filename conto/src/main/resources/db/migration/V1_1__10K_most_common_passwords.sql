CREATE TABLE common_passwords
(
  password VARCHAR(100) NOT NULL PRIMARY KEY
)

AS SELECT * FROM CSVREAD('classpath:/db/migration/10-million-password-list-top-10000.normalized.txt', 'password');

