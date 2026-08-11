-- ============================================================
-- V5: Seed permissions
-- ============================================================

INSERT IGNORE INTO permission (name, description, created_at, updated_at)
VALUES
    ('USER_READ', 'Xem thong tin nguoi dung', NOW(), NOW()),
    ('USER_CREATE', 'Tao nguoi dung', NOW(), NOW()),
    ('USER_UPDATE', 'Cap nhat nguoi dung', NOW(), NOW()),
    ('USER_DELETE', 'Xoa nguoi dung', NOW(), NOW()),
    ('ROLE_READ', 'Xem thong tin role', NOW(), NOW()),
    ('ROLE_CREATE', 'Tao role', NOW(), NOW()),
    ('ROLE_UPDATE', 'Cap nhat role', NOW(), NOW()),
    ('ROLE_DELETE', 'Xoa role', NOW(), NOW()),
    ('PERMISSION_READ', 'Xem thong tin permission', NOW(), NOW()),
    ('PERMISSION_CREATE', 'Tao permission', NOW(), NOW()),
    ('PERMISSION_UPDATE', 'Cap nhat permission', NOW(), NOW()),
    ('PERMISSION_DELETE', 'Xoa permission', NOW(), NOW()),
    ('CATEGORY_READ', 'Xem danh muc', NOW(), NOW()),
    ('CATEGORY_CREATE', 'Tao danh muc', NOW(), NOW()),
    ('CATEGORY_UPDATE', 'Cap nhat danh muc', NOW(), NOW()),
    ('CATEGORY_DELETE', 'Xoa danh muc', NOW(), NOW()),
    ('WALLET_READ', 'Xem vi', NOW(), NOW()),
    ('WALLET_CREATE', 'Tao vi', NOW(), NOW()),
    ('WALLET_UPDATE', 'Cap nhat vi', NOW(), NOW()),
    ('WALLET_DELETE', 'Xoa vi', NOW(), NOW()),
    ('TRANSACTION_READ', 'Xem giao dich', NOW(), NOW()),
    ('TRANSACTION_CREATE', 'Tao giao dich', NOW(), NOW()),
    ('TRANSACTION_UPDATE', 'Cap nhat giao dich', NOW(), NOW()),
    ('TRANSACTION_DELETE', 'Xoa giao dich', NOW(), NOW()),
    ('BUDGET_READ', 'Xem ngan sach', NOW(), NOW()),
    ('BUDGET_CREATE', 'Tao ngan sach', NOW(), NOW()),
    ('BUDGET_UPDATE', 'Cap nhat ngan sach', NOW(), NOW()),
    ('BUDGET_DELETE', 'Xoa ngan sach', NOW(), NOW()),
    ('GOAL_READ', 'Xem muc tieu', NOW(), NOW()),
    ('GOAL_CREATE', 'Tao muc tieu', NOW(), NOW()),
    ('GOAL_UPDATE', 'Cap nhat muc tieu', NOW(), NOW()),
    ('GOAL_DELETE', 'Xoa muc tieu', NOW(), NOW());
