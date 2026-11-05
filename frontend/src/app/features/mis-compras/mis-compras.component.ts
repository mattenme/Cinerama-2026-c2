import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Compra } from '../../core/interfaces/cinerama.interfaces';
import { formatoFechaHora, formatoSoles } from '../../core/services/api';
import { AuthService } from '../../core/services/auth.service';
import { CompraService } from '../../core/services/compra.service';

interface GrupoCompra {
  codigo: string;
  compras: Compra[];
  total: number;
  asientos: string;
}

@Component({
  selector: 'app-mis-compras',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './mis-compras.component.html',
  styleUrl: './mis-compras.component.css'
})
export class MisComprasComponent implements OnInit {
  private compraService = inject(CompraService);
  private auth = inject(AuthService);

  readonly grupos = signal<GrupoCompra[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit(): void {
    const idCliente = this.auth.usuario()?.clienteId;
    if (!idCliente) {
      this.cargando.set(false);
      return;
    }

    this.compraService.listarPorCliente(idCliente).subscribe({
      next: (compras) => {
        this.grupos.set(this.agrupar(compras));
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar tu historial. Verifica que el backend esté encendido.');
        this.cargando.set(false);
      }
    });
  }

  qr(idCompra: number): string {
    return this.compraService.qrUrl(idCompra);
  }

  fecha(iso?: string | null): string {
    return formatoFechaHora(iso);
  }

  soles(valor: number): string {
    return formatoSoles(valor);
  }

  private agrupar(compras: Compra[]): GrupoCompra[] {
    const mapa = new Map<string, Compra[]>();
    for (const c of compras) {
      const clave = c.codigoQr || `compra-${c.idCompra}`;
      mapa.set(clave, [...(mapa.get(clave) ?? []), c]);
    }

    return [...mapa.entries()]
      .map(([codigo, lista]) => ({
        codigo,
        compras: lista,
        total: lista.reduce(
          (suma, c) => suma + Number(c.precio ?? 0) + Number(c.totalProductos ?? 0),
          0
        ),
        asientos: lista
          .map((c) => `${c.filaAsiento ?? ''}${c.numeroAsiento ?? ''}`)
          .filter((t) => t.trim() !== '')
          .join(', ')
      }))
      .sort(
        (a, b) =>
          new Date(b.compras[0]?.fechaCompra ?? 0).getTime() -
          new Date(a.compras[0]?.fechaCompra ?? 0).getTime()
      );
  }
}
