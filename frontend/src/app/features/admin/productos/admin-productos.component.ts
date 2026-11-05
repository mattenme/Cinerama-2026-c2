import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Producto } from '../../../core/interfaces/cinerama.interfaces';
import { formatoSoles, urlImagen } from '../../../core/services/api';
import { AlertService } from '../../../core/services/alert.service';
import { ProductoService } from '../../../core/services/catalogo.service';

@Component({
  selector: 'app-admin-productos',
  standalone: true,
  imports: [FormsModule, RouterLink],
  templateUrl: './admin-productos.component.html',
  styleUrl: './admin-productos.component.css'
})
export class AdminProductosComponent implements OnInit {
  private service = inject(ProductoService);
  private alertas = inject(AlertService);

  readonly productos = signal<Producto[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly guardando = signal(false);
  readonly mostrarForm = signal(false);
  readonly editando = signal<Producto | null>(null);

  modelo = this.vacio();
  archivo: File | null = null;
  nombreArchivo = '';

  ngOnInit(): void {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.service.listarTodos().subscribe({
      next: (productos) => {
        this.productos.set(productos);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la dulcería. Verifica el backend.');
        this.cargando.set(false);
      }
    });
  }

  nuevo() {
    this.modelo = this.vacio();
    this.editando.set(null);
    this.archivo = null;
    this.nombreArchivo = '';
    this.mostrarForm.set(true);
  }

  editar(p: Producto) {
    this.modelo = { ...p };
    this.editando.set(p);
    this.archivo = null;
    this.nombreArchivo = '';
    this.mostrarForm.set(true);
  }

  cerrar() {
    this.mostrarForm.set(false);
  }

  elegirArchivo(evento: Event) {
    const input = evento.target as HTMLInputElement;
    this.archivo = input.files?.[0] ?? null;
    this.nombreArchivo = this.archivo?.name ?? '';
  }

  guardar() {
    if (!this.modelo.nombre?.trim() || !this.modelo.precio || this.modelo.precio <= 0) {
      this.alertas.error('Datos incompletos', 'El nombre y un precio mayor a 0 son obligatorios.');
      return;
    }

    this.guardando.set(true);
    const editando = this.editando();

    const peticion = editando
      ? this.service.actualizar(editando.idProducto!, this.modelo)
      : this.service.crear(this.modelo);

    peticion.subscribe({
      next: (guardado) => {
        if (this.archivo && guardado.idProducto) {
          this.service.subirImagen(guardado.idProducto, this.archivo).subscribe({
            next: () => this.finalizar(editando, true),
            error: () => this.finalizar(editando, false)
          });
          return;
        }
        this.finalizar(editando, true);
      },
      error: (err) => {
        this.guardando.set(false);
        this.alertas.error('No se pudo guardar', err?.error?.message ?? 'Revisa los datos.');
      }
    });
  }

  toggleActivo(p: Producto) {
    this.service.toggleActivo(p.idProducto!).subscribe({
      next: () => this.cargar(),
      error: () => this.alertas.error('No se pudo cambiar el estado')
    });
  }

  async eliminar(p: Producto) {
    const ok = await this.alertas.confirmar('¿Eliminar producto?', `Se eliminará "${p.nombre}".`, 'Sí, eliminar');
    if (!ok) return;

    this.service.eliminar(p.idProducto!).subscribe({
      next: () => {
        this.alertas.exito('Producto eliminado');
        this.cargar();
      },
      error: () => this.alertas.error('No se pudo eliminar')
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

  private finalizar(editando: Producto | null, imagenOk: boolean) {
    this.guardando.set(false);
    this.mostrarForm.set(false);
    if (!imagenOk) {
      this.alertas.error('Se guardó el producto, pero la imagen no se pudo subir');
    } else {
      this.alertas.exito(editando ? 'Producto actualizado' : 'Producto creado');
    }
    this.cargar();
  }

  private vacio(): Producto {
    return { nombre: '', descripcion: '', precio: 0, imagenUrl: null, categoria: 'Snacks', activo: 1 };
  }
}
