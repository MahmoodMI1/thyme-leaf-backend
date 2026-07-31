-- Repurpose the original admin row (id stays 1)
UPDATE users
SET username = 'admin',
    password_hash = '$2a$10$lU/yNEIs6uNOn0/4n8dxLO9CrEXbeng3LOGQCFCNXP1j5XFCw3fzO',
    role = 'ADMIN',
    active = TRUE,
    sessions_invalidated_at = NOW()
WHERE id = 1;

INSERT INTO users (username, password_hash, role) VALUES
                                                      ('chef',  'HASH_FOR_CHEF',  'CHEF'),
                                                      ('staff', 'HASH_FOR_STAFF', 'STAFF');