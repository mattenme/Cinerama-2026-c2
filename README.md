# Cinerama — Sistema de Gestión de Cine

Proyecto del curso de Lenguaje de Programación 2. Sistema de venta de entradas
para un cine: cartelera, selección de butacas, dulcería, promociones, pago,
entrada con código QR y panel de administración.

## Estructura

```
backend/    API REST en Spring Boot 3.4.1 + Java 21 sobre Oracle
frontend/   Aplicación web Angular 18 (standalone) + Bootstrap 5
```

## Requisitos

- Java 21 y Maven (incluido el wrapper `mvnw`)
- Node.js 20+ y npm
- Oracle Database XE con el usuario `cineramaBD`

## Inicio rápido

### 1. Backend

```bash
cd backend
./mvnw spring-boot:run
```

Disponible en <http://localhost:8082/cinerama> (Swagger UI en
`/cinerama/swagger-ui.html`). Requiere la variable de entorno `DB_PASSWORD`.

Al arrancar por primera vez se crean datos de demostración (salas, butacas,
dulcería, promociones y funciones) en las tablas vacías. Se puede desactivar con
`CINERAMA_SEED=false`.

### 2. Frontend

```bash
cd frontend
npm install
npm start
```

Disponible en <http://localhost:4200>. Consume la API a través de
`proxy.config.json` (`/api` → `http://localhost:8082/cinerama`).

## Usuarios de prueba

| Rol           | DNI        | Contraseña |
|---------------|------------|------------|
| Administrador | `00000001` | `admin123` |
| Cliente       | `77889901` | `123456`   |

## Documentación

- Backend: [backend/README.md](backend/README.md) — esquema de base de datos,
  endpoints y correcciones aplicadas.
