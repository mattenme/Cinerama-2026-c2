import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, forkJoin, map, of, switchMap } from 'rxjs';

import { Compra, CompraRequest } from '../interfaces/cinerama.interfaces';
import { API } from './api';

@Injectable({ providedIn: 'root' })
export class CompraService {
  private http = inject(HttpClient);
  private url = `${API}/compras`;

  procesar(request: CompraRequest): Observable<Compra[]> {
    return this.http.post<Compra[]>(`${this.url}/procesar`, request);
  }

  listarPorCliente(idCliente: number): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.url}/cliente/${idCliente}`);
  }

  listarConfirmadasPorFuncion(idFuncion: number): Observable<Compra[]> {
    return this.http.get<Compra[]>(`${this.url}/funcion/${idFuncion}/confirmadas`);
  }

  obtener(id: number): Observable<Compra> {
    return this.http.get<Compra>(`${this.url}/${id}`);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  /** IDs de los asientos ya vendidos para una función. */
  asientosOcupados(idFuncion: number): Observable<number[]> {
    return this.listarConfirmadasPorFuncion(idFuncion).pipe(
      map((compras) =>
        compras
          .map((c) => c.idAsiento)
          .filter((id): id is number => typeof id === 'number')
      )
    );
  }

  /** URL de la imagen PNG del QR de una compra (la genera el backend al vuelo). */
  qrUrl(idCompra: number): string {
    return `${this.url}/${idCompra}/qr.png`;
  }
}
