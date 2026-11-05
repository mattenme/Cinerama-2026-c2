import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Producto } from '../../core/interfaces/cinerama.interfaces';
import { formatoSoles, urlImagen } from '../../core/services/api';
import { CarritoService } from '../../core/services/carrito.service';
import { ProductoService } from '../../core/services/catalogo.service';

@Component({
  selector: 'app-dulceria',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dulceria.component.html',
  styleUrl: './dulceria.component.css'
})
export class DulceriaComponent implements OnInit {
  private service = inject(ProductoService);
  readonly carrito = inject(CarritoService);

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly categoria = signal('TODOS');

  readonly categorias = computed(() =>
    [...new Set(this.productos().map((p) => (p.categoria ?? '').trim()).filter((c) => !!c))].sort()
  );

  readonly filtrados = computed(() => {
    const c = this.categoria();
    return c === 'TODOS' ? this.productos() : this.productos().filter((p) => (p.categoria ?? '').trim() === c);
  });

  ngOnInit(): void {
    this.service.listarActivos().subscribe({
      next: (productos) => {
        this.productos.set(productos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la dulcería. Verifica que el backend esté encendido.');
        this.cargando.set(false);
      }
    });
  }

  imagen(p: Producto): string | null {
    return urlImagen(p.imagenUrl);
  }

  ocultar(evento: Event) {
    (evento.target as HTMLImageElement).style.display = 'none';
  }

  soles(valor: number): string {
    return formatoSoles(valor);
  }
}
