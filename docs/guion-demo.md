# Guion de la demo (10–15 min)

**Antes de empezar:** MySQL corriendo, la app arrancada, navegador en http://localhost:8080, PowerShell en la carpeta del proyecto y Workbench abierto con `sql/02-consultas-demo.sql`.

## 1. Contexto y alcance (2 min)
- Café Aroma toma pedidos en un cuaderno; el objetivo es digitalizar el mostrador.
- Alcance elegido (pista Java/Spring, "elige 2-3"): crear pedido, listar por estado y actualizar estado, más la pantalla de mostrador. Extra pequeño: registro de pago.
- Fuera de alcance: pagos en línea, inventario, "agotado hoy", domicilios.
- Flujo SDD: `requirements.md` → `design.md` → código con IA → revisión humana.

## 2. Demo funcional (6–7 min)
1. **Menú:** cada producto solo muestra sus opciones válidas (el croissant no tiene tamaños; el frappé no tiene Pequeño).
2. **Crear pedido:** "Ana": Latte Grande + shot + deslactosada, y Croissant para llevar → total **$18.000**, calculado por el servidor.
3. **Validación en el backend:** registrar un pedido sin productos → mensaje de error. Marcar a la vez "Para llevar" y "Consumir en el local" → error.
4. **Estados:** avanzar Recibido → En preparación → Listo para recoger. Ya no aparece el botón Cancelar.
5. **La regla no depende de la pantalla:** con PowerShell intentar cancelar ese pedido por API → **409** con mensaje.
   `curl.exe -i -X PATCH http://localhost:8080/api/orders/1/status -H "Content-Type: application/json" -d "@docs/api-ejemplos/estado-cancelado.json"`
6. **Pago:** marcar pagado y entregar; el pedido pasa al historial.

## 3. SQL (1–2 min)
- Mostrar el diagrama ER de `design.md` y por qué `order_item.price` congela el precio cobrado.
- Ejecutar en Workbench: pedidos activos por estado, resumen del día y la comprobación del total (0 filas).

## 4. Uso crítico de la IA (2–3 min)
- Mostrar `docs/uso-critico-ia.md`: qué propuso la IA, qué revisé, qué cambié y cómo lo verifiqué.
- Mencionar que las pruebas automáticas (unitarias e integración) se ejecutaron y pasaron en mi equipo.

## 5. Reflexión (2 min)
- Qué aprendí del flujo SDD y de revisar código generado.
- Siguientes pasos posibles: pantalla "Listo para recoger" para TV, resumen del día en la interfaz.
