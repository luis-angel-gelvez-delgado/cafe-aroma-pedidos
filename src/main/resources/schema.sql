-- Esquema de Café Aroma. Se ejecuta al arrancar; IF NOT EXISTS evita borrar datos existentes.

CREATE TABLE IF NOT EXISTS product (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(80)   NOT NULL UNIQUE,
    category    VARCHAR(20)   NOT NULL,
    base_price  DECIMAL(10,2) NOT NULL,
    CONSTRAINT chk_product_category CHECK (category IN ('BEBIDA_CALIENTE', 'BEBIDA_FRIA', 'PANADERIA')),
    CONSTRAINT chk_product_price    CHECK (base_price >= 0)
);

CREATE TABLE IF NOT EXISTS orders (
    id              BIGINT        AUTO_INCREMENT PRIMARY KEY,
    customer_name   VARCHAR(60)   NOT NULL,
    status          VARCHAR(20)   NOT NULL,
    payment_status  VARCHAR(10)   NOT NULL,
    total           DECIMAL(10,2) NOT NULL,
    created_at      DATETIME      NOT NULL,
    CONSTRAINT chk_orders_status  CHECK (status IN ('RECIBIDO', 'EN_PREPARACION', 'LISTO_PARA_RECOGER', 'ENTREGADO', 'CANCELADO')),
    CONSTRAINT chk_orders_payment CHECK (payment_status IN ('PENDIENTE', 'PAGADO')),
    CONSTRAINT chk_orders_total   CHECK (total >= 0)
);

-- price = precio APLICADO al momento del pedido (no cambia si luego cambia el catálogo).
CREATE TABLE IF NOT EXISTS order_item (
    id          BIGINT        AUTO_INCREMENT PRIMARY KEY,
    order_id    BIGINT        NOT NULL,
    product_id  BIGINT        NOT NULL,
    item_size   VARCHAR(10)   NULL,
    price       DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_item_order   FOREIGN KEY (order_id)   REFERENCES orders (id) ON DELETE CASCADE,
    CONSTRAINT fk_item_product FOREIGN KEY (product_id) REFERENCES product (id),
    CONSTRAINT chk_item_size   CHECK (item_size IS NULL OR item_size IN ('PEQUENO', 'MEDIANO', 'GRANDE')),
    CONSTRAINT chk_item_price  CHECK (price >= 0)
);

CREATE TABLE IF NOT EXISTS order_item_modifier (
    order_item_id  BIGINT       NOT NULL,
    modifier       VARCHAR(25)  NOT NULL,
    PRIMARY KEY (order_item_id, modifier),
    CONSTRAINT fk_modifier_item FOREIGN KEY (order_item_id) REFERENCES order_item (id) ON DELETE CASCADE,
    CONSTRAINT chk_modifier CHECK (modifier IN ('LECHE_DESLACTOSADA', 'SHOT_EXTRA', 'SIN_AZUCAR', 'PARA_LLEVAR', 'CONSUMIR_EN_LOCAL'))
);
