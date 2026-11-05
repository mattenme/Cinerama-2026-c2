# Cinerama - Sistema de Gestión de Cine

Spring Boot 3.4.1 + Java 21. Arquitectura alineada al proyecto de referencia (DTOs como Records).

## Esquema de base de datos (Oracle)

Tablas: ASIENTO, CALIFICACION, CLIENTE, COMPRA, FUNCION, INCIDENCIA, PELICULA, PRODUCTO, PROMOCION, SALA

Conexión en `application.yaml`:
- URL: `jdbc:oracle:thin:@localhost:1521:XE`
- User: `cineramaBD`
- Password: variable de entorno `DB_PASSWORD`

## Arquitectura

- **DTOs = Java Records** con validación (`@NotBlank`, `@NotNull`, etc.)
- Controllers solo usan DTOs (nunca entidades)
- Services convierten Entity ↔ DTO
- Excepciones: `ResourceNotFoundException`, `BadRequestException` + `GlobalExceptionHandler`

## Endpoints principales

| Recurso | Base path |
|---------|-----------|
| Auth | `/api/auth` |
| Películas | `/api/peliculas` |
| Salas | `/api/salas` |
| Asientos | `/api/asientos/sala/{idSala}` |
| Funciones | `/api/funciones` (`GET /pelicula/{idPelicula}` para horarios activos de una película) |
| Productos | `/api/productos` |
| Promociones | `/api/promociones` |
| Clientes | `/api/clientes` |
| Compras | `/api/compras` (procesar: `POST /api/compras/procesar`) |
| Incidencias | `/api/incidencias` |
| Calificaciones | `/api/calificaciones` |

Swagger UI: `http://localhost:8082/cinerama/swagger-ui.html`

## Ejecución

Configura `DB_PASSWORD` en el entorno antes de iniciar la aplicación.

```bash
./mvnw clean package -DskipTests
./mvnw spring-boot:run
```

App: http://localhost:8082/cinerama

## Datos de prueba

| Rol | DNI | Contraseña |
|-----|-----|------------|
| Administrador | `00000001` | `admin123` |
| Cliente | `77889901` | `123456` |

## Frontend Angular (`C:\LP2\cinerama-frontend`)

Angular 18 (standalone) + Bootstrap 5 por CDN. Consume el backend a través de
`proxy.config.json` (`/api` → `http://localhost:8082/cinerama`).

```bash
cd C:\LP2\cinerama-frontend
npm install
npm start          # http://localhost:4200
```

Pantallas: inicio, cartelera, detalle de película con selección de butacas,
dulcería, carrito/checkout con código de promoción, entrada con QR, mis compras,
login/registro con verificación y administración de películas, funciones,
dulcería y promociones.

## Correcciones aplicadas

- `FUNCION.HORA_INICIO` es `TIMESTAMP` en Oracle pero estaba mapeada como `String`:
  crear/editar funciones fallaba con `ORA-01843`. Ahora es `LocalDateTime`.
- `EmailUtil` leía `${mail.*}` en lugar de `${cinerama.mail.*}`: el remitente quedaba
  vacío y todo envío fallaba con "Illegal address". Si el correo no está configurado,
  el código de verificación se devuelve en `codigoDesarrollo` (solo para desarrollo).
- Las imágenes subidas (`/cinerama/uploads/...`) no se servían (404). Nuevo `WebConfig`.
- Dulcería no tenía subida de imágenes: `POST /api/productos/{id}/imagen`.
- El QR se guardaba como imagen base64 en `CODIGO_QR` (no cabe en la columna). Ahora se
  guarda el código corto y la imagen se genera en `GET /api/compras/{id}/qr.png`.
- Una compra de varios asientos comparte código QR: la búsqueda por QR devolvía error
  porque esperaba una sola fila. Ahora devuelve la primera.
- El carrito no aplicaba promociones: `POST /api/compras/procesar` acepta
  `codigoPromocion` y descuenta el porcentaje al precio de cada butaca.
- La cookie de sesión se emitía con `Path=/cinerama`, así que el navegador no la enviaba
  a `/api/...` a través del proxy y la sesión se perdía. Ahora es `Path=/`.
- `CompraDTO` incluye `nombreSala` para poder mostrar la sala en la entrada.
