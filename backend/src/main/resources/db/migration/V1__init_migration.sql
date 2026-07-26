CREATE TABLE users (
   id                      BIGINT AUTO_INCREMENT PRIMARY KEY,
   username                VARCHAR(24)  NOT NULL UNIQUE,
   password_hash           VARCHAR(72)  NOT NULL,
   role                    ENUM('ADMIN','CHEF','STAFF') NOT NULL,
   active                  BOOLEAN      NOT NULL DEFAULT TRUE,
   sessions_invalidated_at TIMESTAMP    NULL,
   created_at              TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);
CREATE TABLE food_items (
    id     BIGINT AUTO_INCREMENT PRIMARY KEY,
    name   VARCHAR(60)   NOT NULL,
    qty    DECIMAL(10,3) NOT NULL,
    unit   VARCHAR(12)   NOT NULL,
    price  DECIMAL(10,2) NOT NULL,
    active BOOLEAN       NOT NULL DEFAULT TRUE
);
CREATE TABLE dishes (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(60)  NOT NULL,
    kind        ENUM('CANAPES','ENTREES','MAINS','SIDES','SWEETS','TEA','COMPONENTS') NOT NULL,
    photo       TEXT         NULL,
    credit      VARCHAR(120) NULL,
    credit_href TEXT         NULL,
    cost        DECIMAL(10,2) NOT NULL DEFAULT 0,
    chef_id     BIGINT       NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_dishes_chef FOREIGN KEY (chef_id) REFERENCES users(id)
);
CREATE TABLE dish_ingredients (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    dish_id      BIGINT        NOT NULL,
    food_item_id BIGINT        NOT NULL,
    amount       DECIMAL(10,3) NOT NULL,
    CONSTRAINT fk_lines_dish FOREIGN KEY (dish_id) REFERENCES dishes(id) ON DELETE CASCADE
);