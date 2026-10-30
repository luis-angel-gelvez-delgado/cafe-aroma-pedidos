-- Menú del reto. Precios en pesos colombianos = SUPUESTOS documentados (requirements.md, S1).
-- base_price = precio de la opción más pequeña (Pequeño en calientes, Mediano en frías).
-- INSERT IGNORE: si el producto ya existe, no lo duplica al reiniciar.

INSERT IGNORE INTO product (id, name, category, base_price) VALUES
    (1,  'Espresso',                  'BEBIDA_CALIENTE', 4000.00),
    (2,  'Americano',                 'BEBIDA_CALIENTE', 4500.00),
    (3,  'Capuchino',                 'BEBIDA_CALIENTE', 6500.00),
    (4,  'Latte',                     'BEBIDA_CALIENTE', 7000.00),
    (5,  'Chocolate Caliente',        'BEBIDA_CALIENTE', 6000.00),
    (6,  'Frappé de Café',            'BEBIDA_FRIA',     10000.00),
    (7,  'Té Helado',                 'BEBIDA_FRIA',     7000.00),
    (8,  'Croissant',                 'PANADERIA',       5000.00),
    (9,  'Muffin de Arándanos',       'PANADERIA',       5500.00),
    (10, 'Sándwich de Jamón y Queso', 'PANADERIA',       9500.00);
