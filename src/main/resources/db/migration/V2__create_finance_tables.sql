-- ============================================================
-- V2: Create finance management tables
-- ============================================================

-- ============================================================
-- 1. CATEGORY
-- ============================================================
CREATE TABLE category (
                          id BIGINT NOT NULL AUTO_INCREMENT,
                          user_id BIGINT NOT NULL,
                          name VARCHAR(100) NOT NULL,
                          type VARCHAR(20) NOT NULL,
                          parent_id BIGINT NULL,
                          created_at DATETIME NOT NULL,
                          updated_at DATETIME NOT NULL,

                          PRIMARY KEY (id),

                          CONSTRAINT fk_category_user
                              FOREIGN KEY (user_id)
                                  REFERENCES users(id),

                          CONSTRAINT fk_category_parent
                              FOREIGN KEY (parent_id)
                                  REFERENCES category(id),

                          CONSTRAINT chk_category_type
                              CHECK (type IN ('INCOME', 'EXPENSE'))
);


-- ============================================================
-- 2. WALLET
-- ============================================================
CREATE TABLE wallet (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        name VARCHAR(100) NOT NULL,
                        balance DECIMAL(19,2) NOT NULL DEFAULT 0,
                        currency VARCHAR(10) NOT NULL DEFAULT 'VND',
                        description VARCHAR(255),
                        status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,

                        PRIMARY KEY (id),

                        CONSTRAINT fk_wallet_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id),

                        CONSTRAINT chk_wallet_status
                            CHECK (status IN ('ACTIVE', 'INACTIVE'))
);


-- ============================================================
-- 3. TRANSACTION
-- ============================================================
CREATE TABLE transaction (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             wallet_id BIGINT NOT NULL,
                             category_id BIGINT NOT NULL,
                             type VARCHAR(20) NOT NULL,
                             amount DECIMAL(19,2) NOT NULL,
                             description VARCHAR(255),
                             transaction_date DATETIME NOT NULL,
                             created_at DATETIME NOT NULL,
                             updated_at DATETIME NOT NULL,

                             PRIMARY KEY (id),

                             CONSTRAINT fk_transaction_wallet
                                 FOREIGN KEY (wallet_id)
                                     REFERENCES wallet(id),

                             CONSTRAINT fk_transaction_category
                                 FOREIGN KEY (category_id)
                                     REFERENCES category(id),

                             CONSTRAINT chk_transaction_type
                                 CHECK (type IN ('INCOME', 'EXPENSE')),

                             CONSTRAINT chk_transaction_amount
                                 CHECK (amount > 0)
);


-- ============================================================
-- 4. BUDGET
-- ============================================================
CREATE TABLE budget (
                        id BIGINT NOT NULL AUTO_INCREMENT,
                        user_id BIGINT NOT NULL,
                        category_id BIGINT NULL,
                        name VARCHAR(100) NOT NULL,
                        amount DECIMAL(19,2) NOT NULL,
                        start_date DATE NOT NULL,
                        end_date DATE NOT NULL,
                        created_at DATETIME NOT NULL,
                        updated_at DATETIME NOT NULL,

                        PRIMARY KEY (id),

                        CONSTRAINT fk_budget_user
                            FOREIGN KEY (user_id)
                                REFERENCES users(id),

                        CONSTRAINT fk_budget_category
                            FOREIGN KEY (category_id)
                                REFERENCES category(id),

                        CONSTRAINT chk_budget_amount
                            CHECK (amount > 0),

                        CONSTRAINT chk_budget_date
                            CHECK (end_date >= start_date)
);


-- ============================================================
-- 5. GOAL
-- ============================================================
CREATE TABLE goal (
                      id BIGINT NOT NULL AUTO_INCREMENT,
                      user_id BIGINT NOT NULL,
                      name VARCHAR(100) NOT NULL,
                      target_amount DECIMAL(19,2) NOT NULL,
                      current_amount DECIMAL(19,2) NOT NULL DEFAULT 0,
                      deadline DATE,
                      status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS',
                      description VARCHAR(255),
                      created_at DATETIME NOT NULL,
                      updated_at DATETIME NOT NULL,

                      PRIMARY KEY (id),

                      CONSTRAINT fk_goal_user
                          FOREIGN KEY (user_id)
                              REFERENCES users(id),

                      CONSTRAINT chk_goal_target_amount
                          CHECK (target_amount > 0),

                      CONSTRAINT chk_goal_current_amount
                          CHECK (current_amount >= 0),

                      CONSTRAINT chk_goal_status
                          CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_category_user
    ON category(user_id);

CREATE INDEX idx_category_parent
    ON category(parent_id);

CREATE INDEX idx_wallet_user
    ON wallet(user_id);

CREATE INDEX idx_transaction_wallet
    ON transaction(wallet_id);

CREATE INDEX idx_transaction_category
    ON transaction(category_id);

CREATE INDEX idx_transaction_date
    ON transaction(transaction_date);

CREATE INDEX idx_budget_user
    ON budget(user_id);

CREATE INDEX idx_budget_category
    ON budget(category_id);

CREATE INDEX idx_goal_user
    ON goal(user_id);