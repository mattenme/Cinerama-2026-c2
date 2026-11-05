import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { map, switchMap } from 'rxjs';

import { Compra } from '../../core/interfaces/cinerama.interfaces';
import { formatoFechaHora, formatoSoles } from '../../core/services/api';
import { CompraService } from '../../core/services/compra.service';

@Component({
  selector: 'app-confirmacion',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './confirmacion.component.html',
  styleUrl: './confirmacion.component.css'
})
export class ConfirmacionComponent implements OnInit {
  private ruta = inject(ActivatedRoute);
  private compraService = inject(CompraService);

  readonly compras = signal<Compra[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly primera = computed(() => this.compras()[0] ?? null);
  readonly total = computed(() =>
    this.compras().reduce(
      (suma, c) => suma + Number(c.precio ?? 0) + Number(c.totalProductos ?? 0),
      0
    )
  );
  readonly asientos = computed(() =>
    this.compras().map((c) => `${c.filaAsiento ?? ''}${c.numeroAsiento ?? ''}`).filter((t) => t.trim() !== '')
  );

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));

    this.compraService
      .obtener(id)
      .pipe(
        switchMap((base) =>
          this.compraService.listarPorCliente(base.idCliente).pipe(map((todas) => ({ base, todas })))
        )
      )
      .subscribe({
        next: ({ base, todas }) => {
          // Una compra de varias butacas genera una fila por butaca con el mismo código QR
          const grupo = todas.filter((c) => c.codigoQr === base.codigoQr && c.idFuncion === base.idFuncion);
          this.compras.set(grupo.length > 0 ? grupo : [base]);
          this.cargando.set(false);
        },
        error: () => {
          this.error.set('No se pudo cargar la entrada. Verifica que el backend esté encendido.');
          this.cargando.set(false);
        }
      });
  }

  qr(idCompra?: number): string {
    return idCompra ? this.compraService.qrUrl(idCompra) : '';
  }

  soles(valor: number): string {
    return formatoSoles(valor);
  }

  fecha(iso?: string | null): string {
    return formatoFechaHora(iso);
  }
}
