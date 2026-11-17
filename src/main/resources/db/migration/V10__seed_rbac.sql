
-- =====================================================
-- 1. Insert predefined roles
-- =====================================================

INSERT INTO roles (name, description, created_at, updated_at)
VALUES
    ('owner', 'Full access to all restaurant resources', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('branch_manager', 'Manages branch operations and staff', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('staff', 'Limited access for daily operations', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
    ON CONFLICT (name) DO NOTHING;


-- =====================================================
-- 2. Insert permissions
-- =====================================================

INSERT INTO permissions (resource, action, created_at)
VALUES
    -- Product permissions
    ('core:product', 'create', CURRENT_TIMESTAMP),
    ('core:product', 'read', CURRENT_TIMESTAMP),
    ('core:product', 'update', CURRENT_TIMESTAMP),

    -- Member permissions
    ('core:member', 'create', CURRENT_TIMESTAMP),
    ('core:member', 'read', CURRENT_TIMESTAMP),
    ('core:member', 'update', CURRENT_TIMESTAMP),
    ('core:member', 'delete', CURRENT_TIMESTAMP),

    -- Branch permissions
    ('core:branch', 'create', CURRENT_TIMESTAMP),
    ('core:branch', 'update', CURRENT_TIMESTAMP),

    -- Restaurant settings permissions
    ('core:restaurant', 'update', CURRENT_TIMESTAMP)

    ON CONFLICT (resource, action) DO NOTHING;


-- =====================================================
-- 3. Owner gets ALL permissions
-- =====================================================

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'owner'
    ON CONFLICT (role_id, permission_id) DO NOTHING;


-- =====================================================
-- 4. Branch Manager permissions
-- =====================================================

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'branch_manager'
  AND (p.resource, p.action) IN (
                                 ('core:product', 'create'),
                                 ('core:product', 'read'),
                                 ('core:product', 'update'),
                                 ('core:member', 'read'),
                                 ('core:branch', 'update')
    )
    ON CONFLICT (role_id, permission_id) DO NOTHING;


-- =====================================================
-- 5. Staff permissions
-- =====================================================

INSERT INTO roles_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
         CROSS JOIN permissions p
WHERE r.name = 'staff'
  AND (p.resource, p.action) IN (
                                 ('core:product', 'read'),
                                 ('core:member', 'read')
    )
    ON CONFLICT (role_id, permission_id) DO NOTHING;
