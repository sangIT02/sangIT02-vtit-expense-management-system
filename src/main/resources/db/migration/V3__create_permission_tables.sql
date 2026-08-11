-- ============================================================
-- V3: Create permission management tables
-- ============================================================

-- ============================================================
-- 1. PERMISSION
-- ============================================================
CREATE TABLE permission (
                            id BIGINT NOT NULL AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255),
                            created_at DATETIME NOT NULL,
                            updated_at DATETIME NOT NULL,

                            PRIMARY KEY (id),

                            CONSTRAINT uk_permission_name
                                UNIQUE (name)
);


-- ============================================================
-- 2. ROLE_PERMISSION
-- ============================================================
CREATE TABLE role_permission (
                                 id BIGINT NOT NULL AUTO_INCREMENT,
                                 role_id BIGINT NOT NULL,
                                 permission_id BIGINT NOT NULL,
                                 created_at DATETIME NOT NULL,
                                 updated_at DATETIME NOT NULL,

                                 PRIMARY KEY (id),

                                 CONSTRAINT fk_role_permission_role
                                     FOREIGN KEY (role_id)
                                         REFERENCES role(id),

                                 CONSTRAINT fk_role_permission_permission
                                     FOREIGN KEY (permission_id)
                                         REFERENCES permission(id),

                                 CONSTRAINT uk_role_permission_role_permission
                                     UNIQUE (role_id, permission_id)
);


-- ============================================================
-- INDEXES
-- ============================================================

CREATE INDEX idx_role_permission_role
    ON role_permission(role_id);

CREATE INDEX idx_role_permission_permission
    ON role_permission(permission_id);
