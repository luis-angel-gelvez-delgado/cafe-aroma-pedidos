# ☕ Café Aroma — Módulo de Pedidos de Mostrador

Aplicación web + API REST para registrar pedidos de mostrador, calcular su total y seguir su estado hasta la entrega. Reto integrador de aprendices (práctica Desarrollo, pista Java/Spring).

- Especificación: [`requirements.md`](requirements.md) · Diseño y diagramas: [`design.md`](design.md)
- Uso crítico de IA: [`docs/uso-critico-ia.md`](docs/uso-critico-ia.md) · Guion de la demo: [`docs/guion-demo.md`](docs/guion-demo.md)

## Tecnologías
Java 21 · Spring Boot 4.1.1 (Web MVC, Data JPA, Thymeleaf) · MySQL 8 · Maven · JUnit 5 + Spring Boot Test (H2 en memoria solo para pruebas) · Git.

## Requisitos previos
| Herramienta | Comprobar | Nota |
|---|---|---|
| JDK 21 | `java -version` | |
| Git | `git --version` | |
| MySQL Server 8.x | `mysql --version` o el menú Inicio | Ver instalación abajo |
| IntelliJ IDEA (gratuito) | — | Trae Maven integrado: no hace falta instalar Maven aparte |

## 1. Instalar MySQL en Windows
1. Descarga **MySQL Community Server 8.4 LTS** (instalador MSI para Windows) desde https://dev.mysql.com/downloads/mysql/.
2. En el asistente de configuración: tipo *Development Computer*, puerto **3306**, define la **contraseña de root** y deja MySQL como **servicio de Windows** que arranca solo.
3. (Opcional) Instala **MySQL Workbench** para ejecutar SQL con interfaz gráfica.
4. Si `mysql` no se reconoce en PowerShell, usa el acceso *MySQL 8.4 Command Line Client* del menú Inicio, o agrega `C:\Program Files\MySQL\MySQL Server 8.4\bin` al PATH.

## 2. Crear la base de datos
Abre `sql/01-crear-base-de-datos.sql`, reemplaza `<TU_CONTRASEÑA>` por una contraseña tuya y ejecútalo como root (Workbench o Command Line Client). Crea la base `cafe_aroma` y el usuario `cafe_app`.

> Las tablas y el menú los crea la aplicación al arrancar con `src/main/resources/schema.sql` y `data.sql`.

## 3. Variables de entorno
La contraseña **nunca** se escribe en el código ni en Git.

| Variable | Obligatoria | Valor por defecto |
|---|---|---|
| `DB_PASSWORD` | Sí | — (si falta, la app no arranca) |
| `DB_USERNAME` | No | `cafe_app` |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | No | `localhost` / `3306` / `cafe_aroma` |

En IntelliJ: **Run → Edit Configurations… → PedidosApplication → Environment variables** → `DB_PASSWORD=tu_contraseña`.

## 4. Ejecutar
1. IntelliJ: **File → Open** → selecciona la carpeta del proyecto (la que tiene `pom.xml`) → *Open as Project*. Espera a que Maven descargue las dependencias.
2. Verifica **File → Project Structure → SDK = 21**.
3. Abre `PedidosApplication.java` y pulsa ▶ (Run).
4. Navega a **http://localhost:8080**.

## 5. Pruebas
| Prueba | Tipo | Necesita MySQL |
|---|---|---|
| `OrderStatusTest` | Unitaria: transiciones de estado | No |
| `ItemPricingTest` | Unitaria: precios, tamaños y modificadores | No |
| `OrderServiceIntegrationTest` | Integración: Spring + JPA + H2 (pedido, estados, pago, consultas) | No |

Ejecutar: clic derecho en `src/test/java` → **Run 'All Tests'**, o en la ventana Maven: **Lifecycle → test**.

**Verificación manual en la pantalla:** crear un pedido de varios ítems, avanzar estados, cancelar, marcar pagado, e intentar registrar un pedido vacío (debe mostrar error).

## 6. Endpoints principales
| Método | Ruta | Respuestas |
|---|---|---|
| GET | `/api/products` | 200 |
| POST | `/api/orders` | 201 · 400 |
| GET | `/api/orders?status=RECIBIDO` | 200 · 400 |
| GET | `/api/orders/{id}` | 200 · 404 |
| PATCH | `/api/orders/{id}/status` | 200 · 400 · 404 · 409 |
| PATCH | `/api/orders/{id}/payment` | 200 · 400 · 404 |

Probar desde PowerShell, en la carpeta del proyecto (`-i` muestra el código HTTP):
```powershell
curl.exe -i http://localhost:8080/api/products
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d "@docs/api-ejemplos/pedido-valido.json"
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d "@docs/api-ejemplos/pedido-sin-items.json"
curl.exe -i -X POST http://localhost:8080/api/orders -H "Content-Type: application/json" -d "@docs/api-ejemplos/pedido-modificador-invalido.json"
curl.exe -i -X PATCH http://localhost:8080/api/orders/1/status -H "Content-Type: application/json" -d "@docs/api-ejemplos/estado-en-preparacion.json"
curl.exe -i -X PATCH http://localhost:8080/api/orders/1/status -H "Content-Type: application/json" -d "@docs/api-ejemplos/estado-listo.json"
curl.exe -i -X PATCH http://localhost:8080/api/orders/1/status -H "Content-Type: application/json" -d "@docs/api-ejemplos/estado-cancelado.json"
curl.exe -i -X PATCH http://localhost:8080/api/orders/1/payment -H "Content-Type: application/json" -d "@docs/api-ejemplos/pago-pagado.json"
curl.exe -i "http://localhost:8080/api/orders?status=LISTO_PARA_RECOGER"
```
Usa `curl.exe` (no `curl`): en PowerShell, `curl` es un alias de otro comando.

## 7. Estructura
```
cafe-aroma/
├── pom.xml
├── requirements.md · design.md · README.md
├── sql/                         creación de BD y consultas de la demo
├── docs/                        uso crítico de IA, guion de demo, ejemplos JSON
└── src/
    ├── main/java/com/cafearoma/pedidos/
    │   ├── model/  repository/  service/  dto/  web/
    ├── main/resources/
    │   ├── application.properties  schema.sql  data.sql
    │   ├── templates/counter.html
    │   └── static/css/styles.css  static/js/order-form.js
    └── test/  (pruebas unitarias e integración + application-test.properties)
```

## Problemas comunes
| Síntoma | Causa probable |
|---|---|
| `Could not resolve placeholder 'DB_PASSWORD'` | Falta la variable de entorno en la configuración de ejecución |
| `Access denied for user 'cafe_app'` | Contraseña distinta a la del script SQL |
| `Communications link failure` | El servicio MySQL no está corriendo |
| `Unknown database 'cafe_aroma'` | No se ejecutó `sql/01-crear-base-de-datos.sql` |
| `Port 8080 was already in use` | Otra app usa el puerto: ciérrala o cambia `server.port` |
