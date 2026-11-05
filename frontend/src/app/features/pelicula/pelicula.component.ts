import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';

import { Asiento, Funcion, Pelicula } from '../../core/interfaces/cinerama.interfaces';
import {
  formatoSoles,
  precioButaca,
  soloFecha,
  soloHora,
  urlImagen
} from '../../core/services/api';
import { AlertService } from '../../core/services/alert.service';
import { CarritoService } from '../../core/services/carrito.service';
import { AsientoService, FuncionService, PeliculaService } from '../../core/services/catalogo.service';
import { CompraService } from '../../core/services/compra.service';

interface DiaFunciones {
  fecha: string;
  funciones: Funcion[];
}

const MAX_BUTACAS = 8;

@Component({
  selector: 'app-pelicula',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './pelicula.component.html',
  styleUrl: './pelicula.component.css'
})
export class PeliculaComponent implements OnInit {
  private ruta = inject(ActivatedRoute);
  private router = inject(Router);
  private peliculaService = inject(PeliculaService);
  private funcionService = inject(FuncionService);
  private asientoService = inject(AsientoService);
  private compraService = inject(CompraService);
  private alertas = inject(AlertService);
  readonly carrito = inject(CarritoService);

  readonly cargando = signal(true);
  readonly error = signal<string | null>(null);
  readonly pelicula = signal<Pelicula | null>(null);
  readonly dias = signal<DiaFunciones[]>([]);
  readonly diaSeleccionado = signal(0);
  readonly funcionSeleccionada = signal<Funcion | null>(null);

  readonly diaActual = computed(() => this.dias()[this.diaSeleccionado()] ?? null);

  readonly cargandoSalas = signal(false);
  readonly asientos = signal<Asiento[]>([]);
  readonly ocupados = signal<Set<number>>(new Set());
  readonly filas = signal<{ fila: string; butacas: Asiento[] }[]>([]);

  readonly totalFunciones = computed(() =>
    this.dias().reduce((total, dia) => total + dia.funciones.length, 0)
  );

  /** Precio de la butaca según la duración de la película (misma regla del backend). */
  readonly precio = computed(() => precioButaca(this.pelicula()?.duracionMinutos));

  ngOnInit(): void {
    const id = Number(this.ruta.snapshot.paramMap.get('id'));

    forkJoin({
      pelicula: this.peliculaService.obtener(id),
      funciones: this.funcionService.listarPorPelicula(id)
    }).subscribe({
      next: ({ pelicula, funciones }) => {
        this.pelicula.set(pelicula);
        this.dias.set(this.agruparPorDia(funciones));
        this.cargando.set(false);
        this.restaurarSeleccionPrevia(pelicula, funciones);
      },
      error: () => {
        this.error.set('No se pudo cargar la película. Verifica que el backend esté encendido.');
        this.cargando.set(false);
      }
    });
  }

  imagen(): string | null {
    return urlImagen(this.pelicula()?.imagenUrl);
  }

  ocultar(evento: Event) {
    (evento.target as HTMLImageElement).style.display = 'none';
  }

  hora(iso: string): string {
    return soloHora(iso);
  }

  soles(valor: number): string {
    return formatoSoles(valor);
  }

  elegirDia(indice: number) {
    this.diaSeleccionado.set(indice);
  }

  elegirFuncion(funcion: Funcion) {
    const pelicula = this.pelicula();
    if (!pelicula) return;

    this.funcionSeleccionada.set(funcion);
    this.carrito.seleccionarFuncion(funcion, pelicula);
    this.cargarButacas(funcion);
  }

  butacaSeleccionada(asiento: Asiento): boolean {
    return this.carrito.esButacaSeleccionada(asiento.idAsiento);
  }

  butacaOcupada(asiento: Asiento): boolean {
    return this.ocupados().has(asiento.idAsiento);
  }

  /** Butacas fuera de servicio (no se pueden vender, pero se muestran). */
  butacaBloqueada(asiento: Asiento): boolean {
    if (this.butacaOcupada(asiento)) return false;
    const estado = (asiento.estado ?? '').toUpperCase();
    return estado.includes('MANTENIMIENTO') || estado.includes('BLOQUE');
  }

  noDisponible(asiento: Asiento): boolean {
    return this.butacaOcupada(asiento) || this.butacaBloqueada(asiento);
  }

  async elegirButaca(asiento: Asiento) {
    if (this.noDisponible(asiento)) return;

    const seleccionadas = this.carrito.asientos().length;
    if (!this.butacaSeleccionada(asiento) && seleccionadas >= MAX_BUTACAS) {
      await this.alertas.info('Máximo de butacas', `Puedes comprar hasta ${MAX_BUTACAS} butacas por función.`);
      return;
    }
    this.carrito.alternarButaca(asiento);
  }

  irAlCarrito() {
    this.router.navigate(['/carrito']);
  }

  // ------------------------------------------------------------------ interno
  private agruparPorDia(funciones: Funcion[]): DiaFunciones[] {
    const ahora = new Date();
    const vigentes = funciones
      .filter((f) => f.activo !== 0)
      .filter((f) => {
        const fecha = new Date(f.horaInicio);
        return !isNaN(fecha.getTime()) && fecha >= new Date(ahora.getTime() - 60 * 60 * 1000);
      })
      .sort((a, b) => new Date(a.horaInicio).getTime() - new Date(b.horaInicio).getTime());

    const mapa = new Map<string, Funcion[]>();
    for (const f of vigentes) {
      const clave = soloFecha(f.horaInicio);
      mapa.set(clave, [...(mapa.get(clave) ?? []), f]);
    }
    return [...mapa.entries()].map(([fecha, lista]) => ({ fecha, funciones: lista }));
  }

  private restaurarSeleccionPrevia(pelicula: Pelicula, funciones: Funcion[]) {
    const enCarrito = this.carrito.funcion();
    if (!enCarrito || this.carrito.pelicula()?.idPelicula !== pelicula.idPelicula) return;

    const vigente = funciones.find((f) => f.idFuncion === enCarrito.idFuncion);
    if (vigente) {
      this.funcionSeleccionada.set(vigente);
      const indice = this.dias().findIndex((d) =>
        d.funciones.some((f) => f.idFuncion === vigente.idFuncion)
      );
      if (indice >= 0) this.diaSeleccionado.set(indice);
      this.cargarButacas(vigente);
    }
  }

  private cargarButacas(funcion: Funcion) {
    if (!funcion.idFuncion || !funcion.idSala) return;

    this.cargandoSalas.set(true);
    this.asientos.set([]);
    this.filas.set([]);

    forkJoin({
      asientos: this.asientoService.listarPorSala(funcion.idSala),
      ocupados: this.compraService.asientosOcupados(funcion.idFuncion)
    }).subscribe({
      next: ({ asientos, ocupados }) => {
        this.asientos.set(asientos);
        this.ocupados.set(new Set(ocupados));
        this.filas.set(this.agruparPorFila(asientos));
        this.cargandoSalas.set(false);
      },
      error: () => {
        this.cargandoSalas.set(false);
        this.alertas.error('No se pudieron cargar las butacas', 'Revisa la conexión con el backend.');
      }
    });
  }

  private agruparPorFila(asientos: Asiento[]) {
    const mapa = new Map<string, Asiento[]>();
    for (const a of [...asientos].sort((x, y) => Number(x.numero) - Number(y.numero))) {
      mapa.set(a.fila, [...(mapa.get(a.fila) ?? []), a]);
    }
    return [...mapa.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([fila, butacas]) => ({ fila, butacas }));
  }
}
