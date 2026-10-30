# Uso crítico de la IA

El reto pide **al menos un caso real** en el que un resultado de la IA se revisó y se corrigió (o rechazó) antes de aceptarlo. Este archivo solo debe contener hechos que ocurrieron.

## Contexto verificable
- La IA (Claude) generó la primera versión de este proyecto el 29/10/2026 a partir de `Reto-Pedidos-Cafe-Aroma.md` y de un prompt maestro escrito por mí.
- La IA **no pudo ejecutar el proyecto con Spring ni MySQL** en su entorno (no tenía acceso a Maven Central). Compiló el código contra versiones simuladas de las librerías y ejecutó la lógica de las pruebas con repositorios en memoria. La primera ejecución real con Spring Boot, H2 y MySQL la hice yo en mi computador.
- La IA declaró sus decisiones de negocio como supuestos (S1–S9 en `requirements.md`) para que yo las revisara.

## Caso documentado
> Completar con lo que realmente pase. Si no hubo corrección, no se inventa: se busca un caso en la revisión.

| Campo | Detalle |
|---|---|
| Qué propuso la IA | |
| Qué detecté al revisarlo | |
| Qué cambio hice | (archivo, línea o commit) |
| Por qué acepté, modifiqué o rechacé | |
| Cómo verifiqué el cambio | (prueba que se ejecutó, captura, consulta SQL) |

## Dónde buscar un caso real (lista de revisión)
- [ ] **Primera ejecución:** ¿compiló y arrancó tal cual? Si algo falló (dependencia, propiedad, SQL), anotar el error y la corrección.
- [ ] **Supuestos de negocio:** ¿estoy de acuerdo con S4 (deslactosada permitida en Espresso) y S7 (entregar con pago pendiente)? Si decido cambiar alguno, es una corrección de criterio de negocio.
- [ ] **Pruebas:** ¿cubren el caso "nombre del cliente vacío" y "transición de Listo a Cancelado" que pide el reto? Ejecutarlas y confirmar el resultado.
- [ ] **SQL:** ejecutar `sql/02-consultas-demo.sql`; la consulta 3 debe devolver 0 filas.
- [ ] **Interfaz:** ¿la pantalla permite algo que el reto no permite? (ej. botón Cancelar en un pedido Listo para recoger).
