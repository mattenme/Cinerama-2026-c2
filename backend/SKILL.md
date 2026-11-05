---
name: cine
description: Use this skill whenever the user wants to do anything with cinema operations. This includes reading movie information, managing tickets, handling seat reservations, processing purchases, checking showtimes, managing cinema rooms, and accessing film data. If the user mentions a movie, cinema, ticket, seat, or showtime, use this skill.
license: Proprietary. Created for Cinerama project.
---

# Cine Guide

## Overview

This guide covers essential cinema operations for the Cinerama project (Spring Boot 3.4.1 + Java 21). It includes movie management, seat reservations, purchase processing, and showtime handling.

## Quick Start

```java
// Read movie information
@RestController
@RequestMapping("/api/peliculas")
public class PeliculaController {
    
    // Get all movies
    @GetMapping
    public List<PeliculaDTO> getAllPeliculas() {
        // Service call to retrieve movies
    }
    
    // Get movie by ID
    @GetMapping("/{id}")
    public PeliculaDTO getPeliculaById(@PathVariable Long id) {
        // Service call to retrieve movie by ID
    }
}
```

## Java Operations

### Movie Management

#### Get All Movies
```java
// Service method to get all movies
public List<PeliculaRecord> listarPeliculas() {
    return peliculaRepository.findAll();
}
```

#### Get Movie by ID
```java
// Service method to get movie by ID
public Optional<PeliculaRecord> obtenerPorId(Long id) {
    return peliculaRepository.findById(id);
}
```

#### Create Movie
```java
// Record-based DTO for movies
public record PeliculaRecord(
    @NotNull @NotBlank String titulo,
    @NotNull @NotBlank String genero,
    @NotNull @NotNull Integer anyoLanzamiento,
    String director,
    Integer duracionMinutos
) {}
```

### Room Management

#### Get All Rooms
```java
// Service method to get all rooms
public List<SalaRecord> listarSalas() {
    return salaRepository.findAll();
}
```

#### Get Room by ID
```java
// Service method to get room by ID
public Optional<SalaRecord> obtenerSalaPorId(Long id) {
    return salaRepository.findById(id);
}
```

### Seat Management

#### Get Seats for a Room
```java
// Service method to get all seats in a room
public List<AsientoRecord> listarAsientosPorSala(Long idSala) {
    return asientoRepository.findBySalaId(idSala);
}
```

### Function/Showtime Management

#### Get All Functions
```java
// Service method to get all functions
public List<FuncionRecord> listarFunciones() {
    return funcionRepository.findAll();
}
```

#### Get Functions by Movie
```java
// Service method to get functions for a specific movie
public List<FuncionRecord> listarFuncionesPorPelicula(Long peliculaId) {
    return funcionRepository.findByPeliculaId(peliculaId);
}
```

### Purchase Processing

#### Process a Purchase
```java
// Endpoint to process purchase
@PostMapping("/compras/procesar")
public ResponseEntity<CompraRecord> procesarCompra(@RequestBody CompraRequest request) {
    // Validation and processing logic
    CompraRecord compra = compraService.procesar(request);
    return ResponseEntity.ok(compra);
}
```

### Client Management

#### Get Client by DNI
```java
// Service method to get client by DNI
public Optional<ClienteRecord> obtenerPorDni(String dni) {
    return clienteRepository.findByDni(dni);
}
```

### Rating/Qualification Management

#### Get All Ratings
```java
// Service method to get all ratings
public List<CalificacionRecord> listarCalificaciones() {
    return calificacionRepository.findAll();
}
```

### Promotion Management

#### Get Promotion by Code
```java
// Service method to get promotion by code
public Optional<PromocionRecord> obtenerPorCodigo(String codigo) {
    return promocionRepository.findByCodigo(codigo);
}
```

### Product Management

#### Get All Products
```java
// Service method to get all products
public List<ProductoRecord> listarProductos() {
    return productoRepository.findAll();
}
```

## Command-Line Operations

### Maven Build
```bash
# Build without tests
./mvnw clean package -DskipTests

# Run the application
./mvnw spring-boot:run
```

### Database Queries (Oracle)
```sql
-- List all movies
SELECT * FROM PELICULA;

-- List all rooms
SELECT * FROM SALA;

-- List all functions for a specific movie
SELECT * FROM FUNCION WHERE id_pelicula = :peliculaId;

-- List available seats for a room and function
SELECT * FROM ASIENTO WHERE id_sala = :idSala AND estado = 'DISPONIBLE';
```

## Common Tasks

| Task | Best Approach | Example |
|------|---------------|---------|
| List movies | Repository call | `peliculaRepository.findAll()` |
| Reserve seats | Update seat status | `asiento.setEstado("RESERVADO")` |
| Process purchase | Service method | `compraService.procesarCompra()` |
| Check showtimes | Functions by movie | `funcionRepository.findByPeliculaId()` |
| Find client by DNI | Repository search | `clienteRepository.findByDni()` |
| Get promotion by code | Code lookup | `promocionRepository.findByCodigo()` |
| Build project | Maven command | `./mvnw clean package` |
| Run application | Maven plugin | `./mvnw spring-boot:run` |

## Next Steps

- For advanced repository operations, see the Cinerama project's entity definitions
- For REST API endpoints, refer to the Cinerama controller structure
- For database schema details, see the Cinerama README
- For DTO/Record patterns, examine the Cinerama project's record definitions

## Project Structure Reference

```
Cinerama/
├── src/main/java/... (main application code)
├── src/main/resources/ (Spring config, application.yaml)
├── pom.xml (Maven dependencies)
└── target/ (build output)
```