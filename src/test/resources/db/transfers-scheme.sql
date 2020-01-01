CREATE SEQUENCE acc_id_seq INCREMENT BY 1 MINVALUE 100000;
CREATE SEQUENCE trans_id_seq INCREMENT BY 1 MINVALUE 100000;

CREATE TABLE account
(
    id          BIGINT      DEFAULT acc_id_seq.NEXTVAL PRIMARY KEY,
    alias       VARCHAR(100),
    type        VARCHAR(20),
    balance     DECIMAL(10, 2),
    currency    VARCHAR(3),
    opened_date DATETIME,
    status      VARCHAR(10) DEFAULT 'ACTIVE'
);

CREATE TABLE transfer
(
    id         BIGINT DEFAULT trans_id_seq.NEXTVAL PRIMARY KEY,
    from_acc   BIGINT,
    to_acc     BIGINT,
    amount     DECIMAL(10, 2),
    currency   VARCHAR(3),
    trans_date DATETIME,
    trans_id   uuid   DEFAULT random_uuid(),
    status     VARCHAR(10),
    FOREIGN KEY (from_acc) REFERENCES account (id),
    FOREIGN KEY (to_acc) REFERENCES account (id)
);