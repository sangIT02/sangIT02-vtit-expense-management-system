-- ============================================================
-- V4: Add permission name check constraint
-- ============================================================

ALTER TABLE permission
    ADD CONSTRAINT chk_permission_name
        CHECK (name IN (
            'USER_READ',
            'USER_CREATE',
            'USER_UPDATE',
            'USER_DELETE',
            'ROLE_READ',
            'ROLE_CREATE',
            'ROLE_UPDATE',
            'ROLE_DELETE',
            'PERMISSION_READ',
            'PERMISSION_CREATE',
            'PERMISSION_UPDATE',
            'PERMISSION_DELETE',
            'CATEGORY_READ',
            'CATEGORY_CREATE',
            'CATEGORY_UPDATE',
            'CATEGORY_DELETE',
            'WALLET_READ',
            'WALLET_CREATE',
            'WALLET_UPDATE',
            'WALLET_DELETE',
            'TRANSACTION_READ',
            'TRANSACTION_CREATE',
            'TRANSACTION_UPDATE',
            'TRANSACTION_DELETE',
            'BUDGET_READ',
            'BUDGET_CREATE',
            'BUDGET_UPDATE',
            'BUDGET_DELETE',
            'GOAL_READ',
            'GOAL_CREATE',
            'GOAL_UPDATE',
            'GOAL_DELETE'
        ));
