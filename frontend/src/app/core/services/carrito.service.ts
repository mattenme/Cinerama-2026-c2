import { Injectable, computed, effect, signal } from '@angular/core';

import { Asiento, Funcion, ItemCompraDulceria, Pelicula, Producto } from '../interfaces/cinerama.interfaces';
import { precioButaca } from './api';

export interface ItemDulceria {
  producto: Producto;
  cantidad: number;
}

interface CarritoEstado {
  funcion: Funcion | null;
  pelicula: Pelicula | null;
  asientos: Asiento[];
  dulceria: ItemDulceria[];
}

const CLAVE = 'cinerama.carrito.v1';
const VACIO: CarritoEstado = { funcion: null, pelicula: null, asientos: [], dulceria: [] };

@Injectable({ providedIn: 'root' })
export class CarritoService {
  private estado = signal<CarritoEstado>(this.leer());

  readonly funcion = computed(() => this.estado().funcion);
  readonly pelicula = computed(() => this.estado().pelicula);
  readonly asientos = computed(() => this.estado().asientos);
  readonly dulceria = computed(() => this.estado().dulceria);

  readonly precioPorButaca = computed(() => precioButaca(this.estado().pelicula?.duracionMinutos));

  readonly totalButacas = computed(
    () => this.estado().asientos.length * this.precioPorButaca()
  );

  readonly totalDulceria = computed(() =>
    this.estado().dulceria.reduce((suma, item) => suma + item.producto.precio * item.cantidad, 0)
  );

  readonly total = computed(() => this.totalButacas() + this.totalDulceria());

  /** Unidades que muestra el globo del carrito: butacas + productos. */
  readonly totalItems = computed(
    () =>
      this.estado().asientos.length +
      this.estado().dulceria.reduce((suma, item) => suma + item.cantidad, 0)
  );

  readonly tieneButacas = computed(() => this.estado().asientos.length > 0);

  constructor() {
    // Guarda automáticamente cualquier cambio del carrito
    effect(() => {
      try {
        localStorage.setItem(CLAVE, JSON.stringify(this.estado()));
      } catch {
        /* almacenamiento lleno o bloqueado: se ignora */
      }
    });
  }

  // ------------------------------------------------------------ butacas
  seleccionarFuncion(funcion: Funcion, pelicula: Pelicula) {
    const actual = this.estado().funcion;
    const cambiaFuncion =
      !actual || actual.idFuncion !== funcion.idFuncion || this.estado().pelicula?.idPelicula !== pelicula.idPelicula;

    this.estado.update((e) => ({
      ...e,
      funcion,
      pelicula,
      asientos: cambiaFuncion ? [] : e.asientos
    }));
  }

  esButacaSeleccionada(idAsiento: number): boolean {
    return this.estado().asientos.some((a) => a.idAsiento === idAsiento);
  }

  alternarButaca(asiento: Asiento) {
    const existe = this.esButacaSeleccionada(asiento.idAsiento);
    this.estado.update((e) => ({
      ...e,
      asientos: existe
        ? e.asientos.filter((a) => a.idAsiento !== asiento.idAsiento)
        : [...e.asientos, asiento]
    }));
  }

  quitarButaca(idAsiento: number) {
    this.estado.update((e) => ({
      ...e,
      asientos: e.asientos.filter((a) => a.idAsiento !== idAsiento)
    }));
  }

  // ---------------------------------------------------------- dulcería
  cantidadProducto(idProducto: number): number {
    return this.estado().dulceria.find((i) => i.producto.idProducto === idProducto)?.cantidad ?? 0;
  }

  agregarProducto(producto: Producto) {
    this.estado.update((e) => {
      const existente = e.dulceria.find((i) => i.producto.idProducto === producto.idProducto);
      const dulceria = existente
        ? e.dulceria.map((i) =>
            i.producto.idProducto === producto.idProducto ? { ...i, cantidad: i.cantidad + 1 } : i
          )
        : [...e.dulceria, { producto, cantidad: 1 }];
      return { ...e, dulceria };
    });
  }

  quitarProducto(idProducto: number) {
    this.estado.update((e) => {
      const actual = e.dulceria.find((i) => i.producto.idProducto === idProducto);
      if (!actual) return e;
      const dulceria =
        actual.cantidad <= 1
          ? e.dulceria.filter((i) => i.producto.idProducto !== idProducto)
          : e.dulceria.map((i) =>
              i.producto.idProducto === idProducto ? { ...i, cantidad: i.cantidad - 1 } : i
            );
      return { ...e, dulceria };
    });
  }

  /** Dulcería que se envía al backend; él resuelve nombre y precio reales. */
  itemsDulceria(): ItemCompraDulceria[] {
    return this.estado().dulceria
      .filter((i) => typeof i.producto.idProducto === 'number')
      .map((i) => ({ idProducto: i.producto.idProducto!, cantidad: i.cantidad }));
  }

  limpiar() {
    this.estado.set({ ...VACIO });
  }

  private leer(): CarritoEstado {
    try {
      const txt = localStorage.getItem(CLAVE);
      if (!txt) return { ...VACIO };
      const datos = JSON.parse(txt) as Partial<CarritoEstado>;
      return {
        funcion: datos.funcion ?? null,
        pelicula: datos.pelicula ?? null,
        asientos: Array.isArray(datos.asientos) ? datos.asientos : [],
        dulceria: Array.isArray(datos.dulceria) ? datos.dulceria : []
      };
    } catch {
      return { ...VACIO };
    }
  }
}
