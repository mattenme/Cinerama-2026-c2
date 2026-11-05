import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { Promocion } from '../../../core/interfaces/cinerama.interfaces';
import { AlertService } from '../../../core/services/alert.service';
import { PromocionService } from '../../../core/services/catalogo.service';

@Component({
  selector: 'app-admin-promociones',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="contenedor admin">
      <div class="admin-cabecera">
        <div>
          <h1 class="titulo-seccion"><i class="bi bi-sliders"></i> Administración</h1>
          <p class="subtitulo">Promociones y códigos de descuento</p>
        </div>
        <button class="btn-acento" type="button" (click)="nueva()">
          <i class="bi bi-plus-lg"></i> Nueva promoción
        </button>
      </div>

      <nav class="admin-tabs">
        <a routerLink="/admin/peliculas">Películas</a>
        <a routerLink="/admin/funciones">Funciones</a>
        <a routerLink="/admin/productos">Dulcería</a>
        <a routerLink="/admin/promociones" class="activo">Promociones</a>
      </nav>

      @if (error()) {
        <div class="aviso">{{ error() }}</div>
      }

      <div class="panel">
        @if (cargando()) {
          <div class="cargando"><span class="spinner-border spinner-border-sm"></span> Cargando promociones…</div>
        } @else if (promociones().length === 0) {
          <div class="vacio"><i class="bi bi-tags"></i> No hay promociones registradas.</div>
        } @else {
          <div class="tabla-scroll">
            <table class="tabla">
              <thead>
                <tr>
                  <th>Código</th>
                  <th>Descripción</th>
                  <th>Descuento</th>
                  <th>Estado</th>
                  <th class="der">Acciones</th>
                </tr>
              </thead>
              <tbody>
                @for (p of promociones(); track p.idPromocion) {
                  <tr>
                    <td><strong class="acento">{{ p.codigo }}</strong></td>
                    <td>{{ p.descripcion || '—' }}</td>
                    <td>{{ p.descuento }}%</td>
                    <td>
                      <span class="pildora" [class.ok]="p.activo === 1" [class.no]="p.activo !== 1">
                        {{ p.activo === 1 ? 'Activa' : 'Inactiva' }}
                      </span>
                    </td>
                    <td>
                      <div class="fila-acciones">
                        <button class="btn-icono" type="button" title="Activar/desactivar" (click)="toggleActivo(p)">
                          <i class="bi" [class.bi-toggle-on]="p.activo === 1" [class.bi-toggle-off]="p.activo !== 1"></i>
                        </button>
                        <button class="btn-icono" type="button" title="Editar" (click)="editar(p)">
                          <i class="bi bi-pencil"></i>
                        </button>
                        <button class="btn-icono peligro" type="button" title="Eliminar" (click)="eliminar(p)">
                          <i class="bi bi-trash"></i>
                        </button>
                      </div>
                    </td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        }
      </div>
    </section>

    @if (mostrarForm()) {
      <div class="modal-fondo" (click)="cerrar()">
        <div class="modal-caja" (click)="$event.stopPropagation()">
          <h3>{{ editando() ? 'Editar promoción' : 'Nueva promoción' }}</h3>

          <div class="grid-2">
            <div>
              <label class="form-label">Código *</label>
              <input class="form-control mb-3" name="codigo" [(ngModel)]="modelo.codigo" maxlength="50"
                     style="text-transform: uppercase">
            </div>
            <div>
              <label class="form-label">Descuento (%) *</label>
              <input class="form-control mb-3" type="number" min="1" max="100"
                     name="descuento" [(ngModel)]="modelo.descuento">
            </div>
          </div>

          <label class="form-label">Descripción</label>
          <input class="form-control mb-3" name="descripcion" [(ngModel)]="modelo.descripcion" maxlength="500">

          <div class="checks">
            <label>
              <input type="checkbox" [ngModel]="modelo.activo === 1"
                     (ngModelChange)="modelo.activo = $event ? 1 : 0" name="activo">
              Activa
            </label>
          </div>

          <div class="modal-pie">
            <button class="btn-fantasma" type="button" (click)="cerrar()">Cancelar</button>
            <button class="btn-acento" type="button" [disabled]="guardando()" (click)="guardar()">
              @if (guardando()) {
                <span class="spinner-border spinner-border-sm"></span> Guardando…
              } @else {
                Guardar
              }
            </button>
          </div>
        </div>
      </div>
    }
  `,
  styles: [`
    .tabla strong { font-size: .92rem; letter-spacing: .5px; }
  `]
})
export class AdminPromocionesComponent implements OnInit {
  private service = inject(PromocionService);
  private alertas = inject(AlertService);

  readonly promociones = signal<Promocion[]>([]);
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly guardando = signal(false);
  readonly mostrarForm = signal(false);
  readonly editando = signal<Promocion | null>(null);

  modelo = this.vacia();

  ngOnInit(): void {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.service.listarTodas().subscribe({
      next: (promociones) => {
        this.promociones.set(promociones);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudieron cargar las promociones. Verifica el backend.');
        this.cargando.set(false);
      }
    });
  }

  nueva() {
    this.modelo = this.vacia();
    this.editando.set(null);
    this.mostrarForm.set(true);
  }

  editar(p: Promocion) {
    this.modelo = { ...p };
    this.editando.set(p);
    this.mostrarForm.set(true);
  }

  cerrar() {
    this.mostrarForm.set(false);
  }

  guardar() {
    if (!this.modelo.codigo?.trim() || !this.modelo.descuento) {
      this.alertas.error('Datos incompletos', 'El código y el descuento son obligatorios.');
      return;
    }

    const cuerpo: Promocion = { ...this.modelo, codigo: this.modelo.codigo.trim().toUpperCase() };
    this.guardando.set(true);
    const editando = this.editando();

    const peticion = editando
      ? this.service.actualizar(editando.idPromocion!, cuerpo)
      : this.service.crear(cuerpo);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.mostrarForm.set(false);
        this.alertas.exito(editando ? 'Promoción actualizada' : 'Promoción creada');
        this.cargar();
      },
      error: (err) => {
        this.guardando.set(false);
        this.alertas.error('No se pudo guardar', err?.error?.message ?? '¿El código ya existe?');
      }
    });
  }

  toggleActivo(p: Promocion) {
    this.service.toggleActivo(p.idPromocion!).subscribe({
      next: () => this.cargar(),
      error: () => this.alertas.error('No se pudo cambiar el estado')
    });
  }

  async eliminar(p: Promocion) {
    const ok = await this.alertas.confirmar('¿Eliminar promoción?', `Se eliminará "${p.codigo}".`, 'Sí, eliminar');
    if (!ok) return;

    this.service.eliminar(p.idPromocion!).subscribe({
      next: () => {
        this.alertas.exito('Promoción eliminada');
        this.cargar();
      },
      error: () => this.alertas.error('No se pudo eliminar')
    });
  }

  private vacia(): Promocion {
    return { codigo: '', descripcion: '', descuento: 10, activo: 1 };
  }
}
