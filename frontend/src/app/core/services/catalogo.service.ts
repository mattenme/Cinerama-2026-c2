import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import {
  Asiento,
  Funcion,
  Pelicula,
  Producto,
  Promocion,
  Sala
} from '../interfaces/cinerama.interfaces';
import { API } from './api';

// ---------------------------------------------------------------- PELÍCULAS
@Injectable({ providedIn: 'root' })
export class PeliculaService {
  private http = inject(HttpClient);
  private url = `${API}/peliculas`;

  listarActivas(): Observable<Pelicula[]> {
    return this.http.get<Pelicula[]>(`${this.url}/activas`);
  }

  listarTodas(): Observable<Pelicula[]> {
    return this.http.get<Pelicula[]>(this.url);
  }

  listarDestacadas(): Observable<Pelicula[]> {
    return this.http.get<Pelicula[]>(`${this.url}/destacadas`);
  }

  obtener(id: number): Observable<Pelicula> {
    return this.http.get<Pelicula>(`${this.url}/${id}`);
  }

  crear(pelicula: Pelicula): Observable<Pelicula> {
    return this.http.post<Pelicula>(this.url, pelicula);
  }

  actualizar(id: number, pelicula: Pelicula): Observable<Pelicula> {
    return this.http.put<Pelicula>(`${this.url}/${id}`, pelicula);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  toggleActivo(id: number): Observable<Pelicula> {
    return this.http.patch<Pelicula>(`${this.url}/${id}/activo`, {});
  }

  toggleDestacado(id: number): Observable<Pelicula> {
    return this.http.patch<Pelicula>(`${this.url}/${id}/destacado`, {});
  }

  subirImagen(id: number, archivo: File): Observable<{ imagenUrl: string }> {
    const datos = new FormData();
    datos.append('file', archivo);
    return this.http.post<{ imagenUrl: string }>(`${this.url}/${id}/imagen`, datos);
  }
}

// -------------------------------------------------------------------- SALAS
@Injectable({ providedIn: 'root' })
export class SalaService {
  private http = inject(HttpClient);
  private url = `${API}/salas`;

  listarTodas(): Observable<Sala[]> {
    return this.http.get<Sala[]>(this.url);
  }

  listarActivas(): Observable<Sala[]> {
    return this.http.get<Sala[]>(`${this.url}/activas`);
  }

  crear(sala: Sala): Observable<Sala> {
    return this.http.post<Sala>(this.url, sala);
  }

  actualizar(id: number, sala: Sala): Observable<Sala> {
    return this.http.put<Sala>(`${this.url}/${id}`, sala);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}

// ----------------------------------------------------------------- FUNCIONES
@Injectable({ providedIn: 'root' })
export class FuncionService {
  private http = inject(HttpClient);
  private url = `${API}/funciones`;

  listarTodas(): Observable<Funcion[]> {
    return this.http.get<Funcion[]>(this.url);
  }

  listarActivas(): Observable<Funcion[]> {
    return this.http.get<Funcion[]>(`${this.url}/activas`);
  }

  listarPorPelicula(idPelicula: number): Observable<Funcion[]> {
    return this.http.get<Funcion[]>(`${this.url}/pelicula/${idPelicula}`);
  }

  obtener(id: number): Observable<Funcion> {
    return this.http.get<Funcion>(`${this.url}/${id}`);
  }

  crear(funcion: Funcion): Observable<Funcion> {
    return this.http.post<Funcion>(this.url, funcion);
  }

  actualizar(id: number, funcion: Funcion): Observable<Funcion> {
    return this.http.put<Funcion>(`${this.url}/${id}`, funcion);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  toggleActivo(id: number): Observable<Funcion> {
    return this.http.patch<Funcion>(`${this.url}/${id}/activo`, {});
  }
}

// ------------------------------------------------------------------ ASIENTOS
@Injectable({ providedIn: 'root' })
export class AsientoService {
  private http = inject(HttpClient);

  listarPorSala(idSala: number): Observable<Asiento[]> {
    return this.http.get<Asiento[]>(`${API}/asientos/sala/${idSala}`);
  }
}

// ----------------------------------------------------------------- PRODUCTOS
@Injectable({ providedIn: 'root' })
export class ProductoService {
  private http = inject(HttpClient);
  private url = `${API}/productos`;

  listarActivos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(`${this.url}/activos`);
  }

  listarTodos(): Observable<Producto[]> {
    return this.http.get<Producto[]>(this.url);
  }

  crear(producto: Producto): Observable<Producto> {
    return this.http.post<Producto>(this.url, producto);
  }

  actualizar(id: number, producto: Producto): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, producto);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  toggleActivo(id: number): Observable<Producto> {
    return this.http.patch<Producto>(`${this.url}/${id}/activo`, {});
  }

  subirImagen(id: number, archivo: File): Observable<{ imagenUrl: string }> {
    const datos = new FormData();
    datos.append('file', archivo);
    return this.http.post<{ imagenUrl: string }>(`${this.url}/${id}/imagen`, datos);
  }
}

// --------------------------------------------------------------- PROMOCIONES
@Injectable({ providedIn: 'root' })
export class PromocionService {
  private http = inject(HttpClient);
  private url = `${API}/promociones`;

  listarActivas(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(`${this.url}/activas`);
  }

  listarTodas(): Observable<Promocion[]> {
    return this.http.get<Promocion[]>(this.url);
  }

  buscarPorCodigo(codigo: string): Observable<Promocion> {
    return this.http.get<Promocion>(`${this.url}/codigo/${encodeURIComponent(codigo)}`);
  }

  crear(promocion: Promocion): Observable<Promocion> {
    return this.http.post<Promocion>(this.url, promocion);
  }

  actualizar(id: number, promocion: Promocion): Observable<Promocion> {
    return this.http.put<Promocion>(`${this.url}/${id}`, promocion);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  toggleActivo(id: number): Observable<Promocion> {
    return this.http.patch<Promocion>(`${this.url}/${id}/activo`, {});
  }
}
