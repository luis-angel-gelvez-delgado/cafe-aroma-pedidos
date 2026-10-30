-- Consultas para la demo y para el encargado de turno. Ejecutar sobre la base cafe_aroma.
USE cafe_aroma;

-- 1. Pedidos activos agrupados por estado
SELECT status, COUNT(*) AS cantidad
FROM orders
WHERE status IN ('RECIBIDO', 'EN_PREPARACION', 'LISTO_PARA_RECOGER')
GROUP BY status;

-- 2. Detalle de un pedido: ítems, tamaño, modificadores y precio aplicado (JOIN de 4 tablas)
SELECT o.id AS pedido, o.customer_name AS cliente, p.name AS producto, i.item_size AS tamano,
       GROUP_CONCAT(m.modifier ORDER BY m.modifier SEPARATOR ', ') AS modificadores,
       i.price AS precio_aplicado
FROM orders o
JOIN order_item i ON i.order_id = o.id
JOIN product p ON p.id = i.product_id
LEFT JOIN order_item_modifier m ON m.order_item_id = i.id
WHERE o.id = 1
GROUP BY o.id, o.customer_name, p.name, i.id, i.item_size, i.price;

-- 3. Comprobar que el total guardado es la suma de los ítems (debe devolver 0 filas)
SELECT o.id, o.total, SUM(i.price) AS suma_items
FROM orders o
JOIN order_item i ON i.order_id = o.id
GROUP BY o.id, o.total
HAVING o.total <> SUM(i.price);

-- 4. Resumen simple del día: productos vendidos (sin cancelados)
SELECT p.name AS producto, COUNT(*) AS unidades, SUM(i.price) AS vendido
FROM order_item i
JOIN orders o ON o.id = i.order_id
JOIN product p ON p.id = i.product_id
WHERE DATE(o.created_at) = CURDATE()
  AND o.status <> 'CANCELADO'
GROUP BY p.name
ORDER BY unidades DESC;

-- 5. Pagos pendientes de pedidos no cancelados
SELECT id, customer_name, status, total
FROM orders
WHERE payment_status = 'PENDIENTE' AND status <> 'CANCELADO';

-- 6. La base también protege las reglas: esta inserción FALLA por el CHECK de estado
-- INSERT INTO orders (customer_name, status, payment_status, total, created_at)
-- VALUES ('Prueba', 'PERDIDO', 'PENDIENTE', 0, NOW());
