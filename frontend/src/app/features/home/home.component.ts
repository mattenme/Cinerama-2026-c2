import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { Pelicula, Promocion } from '../../core/interfaces/cinerama.interfaces';
import { formatoSoles, urlImagen } from '../../core/services/api';
import { PeliculaService, PromocionService } from '../../core/services/catalogo.service';
import { PeliculaCardComponent } from '../../shared/pelicula-card/pelicula-card.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, PeliculaCardComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit {
  private peliculaService = inject(PeliculaService);
  private promocionService = inject(PromocionService);

  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly peliculas = signal<Pelicula[]>([]);
  readonly promociones = signal<Promocion[]>([]);

  readonly portada = computed(() => this.peliculas()[0] ?? null);
  readonly imagenPortada = computed(() => urlImagen(this.portada()?.imagenUrl));

  ngOnInit(): void {
    this.cargar();
  }

  cargar() {
    this.cargando.set(true);
    this.error.set(null);

    forkJoin({
      peliculas: this.peliculaService.listarActivas(),
      promociones: this.promocionService.listarActivas()
    }).subscribe({
      next: ({ peliculas, promociones }) => {
        const ordenadas = [...peliculas].sort((a, b) => (b.destacado ?? 0) - (a.destacado ?? 0));
        this.peliculas.set(ordenadas);
        this.promociones.set(promociones);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la cartelera. Verifica que el backend esté encendido en http://localhost:8082/cinerama');
        this.cargando.set(false);
      }
    });
  }

  imagen(url?: string | null): string | null {
    return urlImagen(url);
  }

  soles(valor: number): string {
    return formatoSoles(valor);
  }

  ocultar(evento: Event) {
    (evento.target as HTMLImageElement).style.display = 'none';
  }
}
