# Trabajo Práctico Grupal 2026: Sistema de Gestión "Epicentro Gourmet"

## Grupo 5

### Integrantes:

 - Daira Mazza ([dairamazza1](https://github.com/dairamazza1))
 - Malena Lescano ([MalenaLesc](https://github.com/MalenaLesc))
 - Lautaro Gonzalez ([lautarogonzalez1405](https://github.com/lautarogonzalez1405))
 - Facundo D'Addese ([facudaddese](https://github.com/facudaddese))

---

## Tecnologías utilizadas

- Java
- Spring Boot
- Maven
- MySQL
- Spring Data JPA
- Spring Security
- Thymeleaf
- Git / GitHub

---

## Diagrama de clases

El siguiente diagrama representa el modelo de dominio utilizado para el desarrollo de la aplicación.

[Ver diagrama de clases completo](https://drive.google.com/file/d/1j4faJcw5gdOJ7YDLjNtQepBoVyPtJ_gL/view?usp=sharing)

[Diagrama de clases]<img width="998" height="700" alt="image" src="https://github.com/user-attachments/assets/a695f503-1c0d-44fc-b4c4-65df6ca25490" />


> El diagrama puede sufrir modificaciones durante el desarrollo del Hito 2.

---

## Casos de uso

El listado completo de casos de uso, dependencias, estado y distribución de tareas se encuentra documentado en la siguiente planilla:

[Ver planilla de Casos de Uso](https://drive.google.com/file/d/1Ak1v6w2vNtKnWrkb6Q2C7bsf9jNrcL3i/view?usp=sharing)

### Casos de uso implementados

| ID | Caso de uso | Responsable | Pull Request |
|---|---|---|---|
| CU01 | Iniciar sesión | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/7) |
| CU02 | Registrar Responsable con Unidad de Venta | Daira Mazza | [PR](LINK_PR) |
| CU03 | Consultar festivales | Lautaro Gonzalez | [PR](LINK_PR) |
| CU04 | Ver unidades de venta de un festival | Lautaro Gonzalez | [PR](LINK_PR) |
| CU05 | Generar ranking de unidades por recaudación | Lautaro Gonzalez | [PR](LINK_PR) |
| CU06 | Obtener unidades con mayor canon | Daira Mazza | [PR](LINK_PR) |
| CU07 | Dar de alta un empleado | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/a962e773d1188f74e76cc069acd2984f0dd94b83) |
| CU08 | Modificar empleado | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/93108e2c2e6333a00937c0fbccc9a16713b51cf3) |
| CU09 | Dar de baja lógica a un empleado | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/394ad0047f6e68926b33e195ddf5deb05b66e353) |
| CU10 | Reactivar empleado eliminado | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/7527de9114c7cbdbff3ed28d9be40aad4d75f093) |
| CU11 | Filtrar / listar empleados activos | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/25a4f92f72d414176bd21bff8f0ef1c511017322) |
| CU12 | Dar de alta un plato | Daira Mazza | [PR](LINK_PR) |
| CU13 | Calcular canon de una unidad | Daira Mazza | [PR](LINK_PR) |
| CU14 | Liquidar haberes | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/8) [Fix](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/13)|
| CU15 | Generar reporte de recaudación | Lautaro Gonzalez | [PR](LINK_PR) |
| CU16 | Calcular rentabilidad neta | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/25) |
| CU17 | Calcular rentabilidad neta entre dos fechas | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/29) |
| CU18 | Obtener plato estrella | Daira Mazza | [PR](LINK_PR) |
| CU19 | Consultar historial de trabajo | Daira Mazza | [PR](LINK_PR) |
| CU20 | Consultar sueldo recibido por festival | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/16)|
| CU21 | Descargar recibo de sueldo en PDF | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/19) |
| CU22 | Consultar festival y unidad de trabajo actual | Lautaro Gonzalez | [PR](LINK_PR) |
| CU23 | Cerrar caja | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/dd1badf754994f40462412694c71f95acf8abb4b) |
| CU24 | Alta Pedido | Daira Mazza | [PR](LINK_PR) |
| CU25 | Fix: Actualizar data.sql | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/6c2cd862de34967c742ee47c57fb0be0861d1d25) |
| CU26 | Fix: Generar código de unidad de venta | Lautaro Gonzalez | [PR](LINK_PR) |
| CU27 | Asignar rol de responsable a la unidad de venta | Daira Mazza | [PR](LINK_PR) |
| CU28 | Registrar tipo de unidad de venta | Daira Mazza | [PR](LINK_PR) |
| CU29 | fix: Landing principal | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/20) |
| CU30 | Consultar landing de reportes | Daira Mazza | [PR](LINK_PR) |
| CU31 | Consultar ranking de unidades de venta más vendidas | Lautaro Gonzalez | [PR](LINK_PR) |
| CU32 | Habilitar pago de sueldos | Malena Lescano | [PR](LINK_PR) |
| CU33 | fix: Columna detalle | Facundo D'Addese | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/commit/6f1e84fe813d05272059d67afc39e6e1711b355c) |
| CU34 | Fix: Dos vistas de lista de UDVs | Lautaro Gonzalez | [PR](LINK_PR) |
| CU35 | Fix: sacar sueldo de Alta empleado y seleccion de UDV | Malena Lescano | [PR](LINK_PR) |
| CU36 | Validaciones de Alta Empleado | Malena Lescano | [PR](https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05/pull/30) |


> Los responsables y links a Pull Requests se actualizarán a medida que avance el desarrollo.

## Datos de prueba

Para probar los distintos casos de uso se incluye un script con datos iniciales.

El script contiene datos correspondientes a:

- Festivales.
- Usuarios con distintos roles.
- Unidades de venta.
- Responsables.
- Empleados.
- Costos.

Ubicación:

```text
src/main/resources/data.sql
```
---
## Ejecución del proyecto
Requisitos previos
Para ejecutar la aplicación es necesario tener instalado:
- Java
- MySQL
- Mave

## Base de datos

Crear una base de datos MySQL para la aplicación. Ejemplo:
```text
CREATE DATABASE epicentro_gourmet;
```

## Variables de entorno
La aplicación utiliza variables de entorno para la configuración de la base de datos y otros servicios. Ejemplo:
```text
DB_URL=jdbc:mysql://localhost:3306/epicentro_gourmet
DB_USERNAME=root
DB_PASSWORD=contraseña
```
Para el envío de e-mails:
```text
MAIL_USERNAME=correo
MAIL_PASSWORD=contraseña
```
> No subir contraseñas ni credenciales reales al repositorio.

## Ejecutar la aplicación
Clonar el repositorio:
```text
git clone https://github.com/facudaddese/TPGrupalOO2Hito2Grupo05.git
```
Ingresar al proyecto:
```text
cd TPGrupalOO2Hito2Grupo05
```
Instalar dependencias y compilar:
```text
mvn clean install
```
Ejecutar Spring Boot:
```text
mvn spring-boot:run
```
Una vez iniciada la aplicación, acceder desde:
```text
http://localhost:8080
```


