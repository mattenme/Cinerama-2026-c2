import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Pelicula } from '../../core/interfaces/cinerama.interfaces';
import { urlImagen } from '../../core/services/api';

@Component({
  selector: 'app-pelicula-card',
  standalone: true,
  imports: [RouterLink],
  template: `
    <a class="tarjeta" [routerLink]="['/pelicula', pelicula.idPelicula]">
      <div class="poster">
        <div class="respaldo">
          <i class="bi bi-camera-reels"></i>
          <span>{{ pelicula.titulo }}</span>
        </div>
        @if (imagen()) {
          <img [src]="imagen()!" [alt]="pelicula.titulo" (error)="ocultar($event)">
        }
        @if (pelicula.destacado === 1) {
          <span class="cinta">Destacada</span>
        }
      </div>
      <div class="datos">
        <h3>{{ pelicula.titulo }}</h3>
        <p class="meta">
          @if (pelicula.genero) { <span>{{ pelicula.genero }}</span> }
          <span><i class="bi bi-clock"></i> {{ pelicula.duracionMinutos }} min</span>
        </p>
        <span class="ver">Ver horarios <i class="bi bi-arrow-right"></i></span>
      </div>
    </a>
  `,
  styles: [`
    .tarjeta { display: block; color: var(--texto); }
    .poster { transition: transform .18s ease, box-shadow .18s ease; }
    .tarjeta:hover .poster { transform: translateY(-2px); box-shadow: 0 8px 18px rgba(0, 0, 0, .3); }
    .tarjeta:hover .ver { color: var(--acento); }
    .cinta {
      position: absolute;
      top: 10px;
      left: 10px;
      background: var(--acento);
      color: #fff;
      font-size: .66rem;
      font-weight: 800;
      letter-spacing: .8px;
      text-transform: uppercase;
      padding: 3px 9px;
      border-radius: 3px;
      z-index: 2;
    }
    .datos { padding: 12px 2px 0; }
    h3 { font-size: .98rem; margin: 0 0 6px; font-weight: 600; line-height: 1.25; }
    .meta { display: flex; flex-wrap: wrap; gap: 10px; color: var(--muted); font-size: .78rem; margin: 0 0 10px; }
    .ver { font-size: .78rem; font-weight: 600; color: var(--muted); letter-spacing: 0; }
  `]
})
export class PeliculaCardComponent {
  @Input({ required: true }) pelicula!: Pelicula;

  imagen(): string | null {
    return urlImagen(this.pelicula.imagenUrl);
  }

  ocultar(evento: Event) {
    (evento.target as HTMLImageElement).style.display = 'none';
  }
}
