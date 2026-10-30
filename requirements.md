# requirements.md — Módulo de Pedidos · Café Aroma

Fuente de verdad: `Reto-Pedidos-Cafe-Aroma.md`. Lo que no está aquí está fuera de alcance.

## Objetivo
Digitalizar el cuaderno de pedidos del mostrador: registrar pedidos con sus ítems, calcular el total y seguir su estado hasta la entrega, sin pagos en línea ni integraciones.

## Alcance (pista Backend Java/Spring del reto: "elige 2-3")
| # | Capacidad | Origen en el reto |
|---|---|---|
| F1 | Crear pedido con ítems, tamaños y modificadores; total calculado por el backend | Alcance Desarrollo: "crear pedido"; reglas 1 y 2 |
| F2 | Listar pedidos por estado | Alcance Desarrollo: "listar pedidos por estado" |
| F3 | Actualizar estado (incluye cancelar) con transiciones validadas | Alcance Desarrollo: "actualizar estado"; flujo de estados |
| F4 | Pantalla de mostrador: menú, armado del pedido, pedidos activos | Alcance Desarrollo: "pantalla de mostrador" |
| F5 | Registrar pago pendiente/pagado | Regla 3 (extensión pequeña) |
| F6 | Consultar el menú con precios | Historia "ver el menú con precios" (Must) |

## Criterios de aceptación
- **CA1** Un pedido sin ítems se rechaza con un mensaje claro (API: 400).
- **CA2** Un pedido sin nombre de cliente se rechaza (400).
- **CA3** El total del pedido es la suma de los precios de sus ítems y lo calcula el servidor (el cliente no envía precios).
- **CA4** Una bebida sin tamaño, o con un tamaño que su categoría no ofrece (ej. Frappé Pequeño), se rechaza (400).
- **CA5** Un producto de panadería con tamaño se rechaza (400).
- **CA6** Un modificador que no corresponde a la categoría (ej. shot extra en croissant) se rechaza (400).
- **CA7** Solo se permiten las transiciones Recibido → En preparación → Listo para recoger → Entregado, paso a paso.
- **CA8** Cancelar solo es posible desde Recibido o En preparación; desde Listo para recoger se rechaza (API: 409).
- **CA9** El pago puede marcarse Pendiente o Pagado; no en pedidos cancelados.
- **CA10** Todo pedido nuevo inicia en Recibido y con pago Pendiente.
- **CA11** Cada pedido tiene un número visible (`#0001`) y el nombre del cliente.

## Reglas de negocio (del reto)
1. Un pedido tiene 1 o más ítems; cada ítem es un producto con tamaño (si aplica) y 0 o más modificadores.
2. Total = suma de los ítems; tamaño y modificadores pueden afectar el precio.
3. Pago pagado/pendiente en mostrador, sin pasarela.
4. Sin inventario ni mermas.
5. Sin domicilios ni reservas.
6. Nombre del cliente o número de orden como identificador visible.

## Fuera de alcance
Autenticación y roles en el sistema · pasarelas de pago · inventario · "agotado hoy" (marcado *Won't* en el reto) · domicilios y reservas · notificaciones · reportes financieros · edición de pedidos ya creados · cantidades por ítem.

## Supuestos (pendientes de validar con el mentor)
- **S1 Precios base (COP):** Espresso 4.000 · Americano 4.500 · Capuchino 6.500 · Latte 7.000 · Chocolate Caliente 6.000 · Frappé de Café 10.000 · Té Helado 7.000 · Croissant 5.000 · Muffin 5.500 · Sándwich 9.500.
- **S2 Tamaños:** el precio base es el del tamaño más pequeño disponible. Calientes: Mediano +1.500, Grande +3.000. Frías (base = Mediano): Grande +1.500.
- **S3 Modificadores:** Leche deslactosada +1.000 · Shot extra +2.000 · Sin azúcar, Para llevar y Para consumir en el local: sin costo.
- **S4** Los modificadores se validan por categoría, no por producto (ej. se permite deslactosada en un Espresso).
- **S5** "Para llevar" y "Para consumir en el local" son excluyentes y opcionales.
- **S6** El nombre del cliente es obligatorio (máx. 60 caracteres); además se muestra el número de pedido.
- **S7** Un pedido puede entregarse con pago pendiente: el sistema solo registra el estado del pago.
- **S8** Cada ítem es una unidad; para dos lattes se agregan dos ítems.
- **S9** Cancelado es un estado final.
