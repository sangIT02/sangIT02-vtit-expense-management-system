-- ============================================================
-- V6: Create api_permission table for DB-driven API authorization
-- ============================================================

CREATE TABLE api_permission (
                                id BIGINT NOT NULL AUTO_INCREMENT,
                                http_method VARCHAR(10) NOT NULL,
                                url_pattern VARCHAR(255) NOT NULL,
                                permission_name VARCHAR(100) NOT NULL,
                                description VARCHAR(255),
                                enabled BOOLEAN NOT NULL DEFAULT TRUE,
                                created_at DATETIME NOT NULL,
                                updated_at DATETIME NOT NULL,

                                PRIMARY KEY (id),

                                CONSTRAINT fk_api_permission_permission
                                    FOREIGN KEY (permission_name)
                                        REFERENCES permission(name),

                                CONSTRAINT uk_api_permission_method_url
                                    UNIQUE (http_method, url_pattern)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_api_permission_method_url
    ON api_permission(http_method, url_pattern);

CREATE INDEX idx_api_permission_permission_name
    ON api_permission(permission_name);


-- ============================================================
-- Seed mappings for existing endpoints
-- ============================================================

INSERT IGNORE INTO api_permission (http_method, url_pattern, permission_name, description, enabled, created_at, updated_at)
VALUES
    -- Wallets
    ('POST',   '/api/v1/wallets',                    'WALLET_CREATE',      'Tao vi',                         TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/wallets',                    'WALLET_READ',        'Lay danh sach vi',               TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/wallets/*',                  'WALLET_READ',        'Lay chi tiet vi',                TRUE, NOW(), NOW()),
    ('PUT',    '/api/v1/wallets/*',                  'WALLET_UPDATE',      'Cap nhat vi',                    TRUE, NOW(), NOW()),
    ('DELETE', '/api/v1/wallets/*',                  'WALLET_DELETE',      'Xoa vi',                         TRUE, NOW(), NOW()),

    -- Categories
    ('POST',   '/api/v1/categories',                 'CATEGORY_CREATE',    'Tao danh muc',                   TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/categories',                 'CATEGORY_READ',      'Lay danh sach danh muc',         TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/categories/tree',            'CATEGORY_READ',      'Lay cay danh muc',               TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/categories/*',               'CATEGORY_READ',      'Lay chi tiet danh muc',          TRUE, NOW(), NOW()),
    ('PUT',    '/api/v1/categories/*',               'CATEGORY_UPDATE',    'Cap nhat danh muc',              TRUE, NOW(), NOW()),
    ('DELETE', '/api/v1/categories/*',               'CATEGORY_DELETE',    'Xoa danh muc',                   TRUE, NOW(), NOW()),

    -- Transactions
    ('POST',   '/api/v1/transactions',               'TRANSACTION_CREATE', 'Tao giao dich',                  TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/transactions',               'TRANSACTION_READ',   'Lay danh sach giao dich',        TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/transactions/export',        'TRANSACTION_READ',   'Xuat giao dich',                 TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/transactions/*',             'TRANSACTION_READ',   'Lay chi tiet giao dich',         TRUE, NOW(), NOW()),
    ('PUT',    '/api/v1/transactions/*',             'TRANSACTION_UPDATE', 'Cap nhat giao dich',             TRUE, NOW(), NOW()),
    ('DELETE', '/api/v1/transactions/*',             'TRANSACTION_DELETE', 'Xoa giao dich',                  TRUE, NOW(), NOW()),

    -- Budgets
    ('POST',   '/api/v1/budgets',                    'BUDGET_CREATE',      'Tao ngan sach',                  TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/budgets',                    'BUDGET_READ',        'Lay danh sach ngan sach',        TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/budgets/*',                  'BUDGET_READ',        'Lay chi tiet ngan sach',         TRUE, NOW(), NOW()),
    ('PUT',    '/api/v1/budgets/*',                  'BUDGET_UPDATE',      'Cap nhat ngan sach',             TRUE, NOW(), NOW()),
    ('DELETE', '/api/v1/budgets/*',                  'BUDGET_DELETE',      'Xoa ngan sach',                  TRUE, NOW(), NOW()),

    -- Goals
    ('POST',   '/api/v1/goals',                      'GOAL_CREATE',        'Tao muc tieu',                   TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/goals',                      'GOAL_READ',          'Lay danh sach muc tieu',         TRUE, NOW(), NOW()),
    ('GET',    '/api/v1/goals/*',                    'GOAL_READ',          'Lay chi tiet muc tieu',          TRUE, NOW(), NOW()),
    ('PUT',    '/api/v1/goals/*',                    'GOAL_UPDATE',        'Cap nhat muc tieu',              TRUE, NOW(), NOW()),
    ('POST',   '/api/v1/goals/*/contributions',      'GOAL_UPDATE',        'Dong gop muc tieu',              TRUE, NOW(), NOW()),
    ('DELETE', '/api/v1/goals/*',                    'GOAL_DELETE',        'Xoa muc tieu',                   TRUE, NOW(), NOW()),

    -- Reconciliation
    ('GET',    '/api/v1/reconciliation/wallets/*',   'TRANSACTION_READ',   'Doi soat vi',                    TRUE, NOW(), NOW());
