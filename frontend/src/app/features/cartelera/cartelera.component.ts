import { Component, OnInit, computed, inject, signal } from '@angular/core';

import { Pelicula } from '../../core/interfaces/cinerama.interfaces';
import { PeliculaService } from '../../core/services/catalogo.service';
import { PeliculaCardComponent } from '../../shared/pelicula-card/pelicula-card.component';

@Component({
  selector: 'app-cartelera',
  standalone: true,
  imports: [PeliculaCardComponent],
  template: `
    <section class="contenedor seccion">
      <h1 class="titulo-seccion">Cartelera</h1>
      <p class="subtitulo">Selecciona una película para ver sus funciones y butacas disponibles</p>

      @if (cargando()) {
        <div class="cargando"><span class="spinner-border spinner-border-sm"></span> Cargando…</div>
      } @else if (error()) {
        <div class="aviso">{{ error() }}</div>
      } @else if (peliculas().length === 0) {
        <div class="vacio"><i class="bi bi-film"></i> No hay películas activas por ahora.</div>
      } @else {
        <div class="filtros">
          <button class="chip" [class.activo]="genero() === 'TODOS'" (click)="genero.set('TODOS')">Todas</button>
          @for (g of generos(); track g) {
            <button class="chip" [class.activo]="genero() === g" (click)="genero.set(g)">{{ g }}</button>
          }
        </div>

        <div class="grilla">
          @for (p of filtradas(); track p.idPelicula) {
            <app-pelicula-card [pelicula]="p"></app-pelicula-card>
          }
        </div>
      }
    </section>
  `,
  styles: [`
    .filtros { display: flex; flex-wrap: wrap; gap: 8px; margin: 20px 0 26px; }
    .chip {
      background: var(--surface);
      border: 1px solid var(--linea);
      color: var(--muted);
      border-radius: 999px;
      padding: 6px 14px;
      font-size: .82rem;
      font-weight: 600;
    }
    .chip:hover { color: var(--texto); border-color: var(--acento); }
    .chip.activo { background: var(--acento); color: #fff; border-color: var(--acento); }
    .grilla { display: grid; grid-template-columns: repeat(4, 1fr); gap: 22px; }
    @media (max-width: 980px) { .grilla { grid-template-columns: repeat(3, 1fr); } }
    @media (max-width: 620px) { .grilla { grid-template-columns: repeat(2, 1fr); gap: 14px; } }
  `]
})
export class CarteleraComponent implements OnInit {
  private service = inject(PeliculaService);

  readonly peliculas = signal<Pelicula[]>([]);
  readonly genero = signal('TODOS');
  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);

  readonly generos = computed(() =>
    [...new Set(this.peliculas().map((p) => (p.genero ?? '').trim()).filter((g) => !!g))].sort()
  );

  readonly filtradas = computed(() => {
    const g = this.genero();
    return g === 'TODOS' ? this.peliculas() : this.peliculas().filter((p) => (p.genero ?? '').trim() === g);
  });

  ngOnInit(): void {
    this.service.listarActivas().subscribe({
      next: (peliculas) => {
        this.peliculas.set(peliculas);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la cartelera. Verifica que el backend esté encendido.');
        this.cargando.set(false);
      }
    });
  }
}
