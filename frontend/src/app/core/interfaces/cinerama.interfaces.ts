// Las interfaces reflejan exactamente los DTO (records) que devuelve el backend.

export interface Pelicula {
  idPelicula?: number;
  titulo: string;
  duracionMinutos: number;
  genero?: string | null;
  sinopsis?: string | null;
  imagenUrl?: string | null;
  destacado?: number;
  activo?: number;
}

export interface Sala {
  idSala?: number;
  nombre: string;
  tipo?: string | null;
  capacidadTotal: number;
  activo?: number;
}

export interface Funcion {
  idFuncion?: number;
  idPelicula: number;
  tituloPelicula?: string | null;
  idSala: number;
  nombreSala?: string | null;
  horaInicio: string; // ISO: 2026-10-27T18:00:00
  estado?: string | null;
  activo?: number;
}

export interface Asiento {
  idAsiento: number;
  idSala: number;
  nombreSala?: string | null;
  fila: string;
  numero: number;
  estado?: string | null;
}

export interface Producto {
  idProducto?: number;
  nombre: string;
  descripcion?: string | null;
  precio: number;
  imagenUrl?: string | null;
  categoria?: string | null;
  activo?: number;
}

export interface Promocion {
  idPromocion?: number;
  codigo: string;
  descripcion?: string | null;
  descuento: number;
  activo?: number;
}

export interface Compra {
  idCompra: number;
  idCliente: number;
  nombreCliente?: string | null;
  idFuncion: number;
  tituloPelicula?: string | null;
  nombreSala?: string | null;
  horaInicio?: string | null;
  idAsiento?: number | null;
  filaAsiento?: string | null;
  numeroAsiento?: number | null;
  precio: number;
  /** Dulcería del pedido; llega solo en la primera butaca del grupo. */
  totalProductos?: number | null;
  metodoPago?: string | null;
  estado?: string | null;
  codigoQr?: string | null;
  fechaCompra?: string | null;
  productos?: string | null;
}

export interface ItemCompraDulceria {
  idProducto: number;
  cantidad: number;
}

export interface CompraRequest {
  clienteId: number;
  funcionId: number;
  asientosIds: number[];
  metodoPago: string;
  /** El backend calcula el precio de la dulcería con los precios de la base de datos. */
  productos?: ItemCompraDulceria[] | null;
  codigoPromocion?: string | null;
}

export interface Cliente {
  idCliente?: number;
  dni: string;
  nombre: string;
  email: string;
  telefono?: string | null;
  rol?: string | null;
  activo?: number;
  verificado?: number;
}

export interface AuthResponse {
  success: boolean;
  message?: string | null;
  mensaje?: string | null;
  rol?: string | null;
  clienteId?: number | null;
  nombre?: string | null;
  email?: string | null;
  needVerify?: boolean;
  /** Solo llega cuando el correo del servidor no está configurado (modo local). */
  codigoDesarrollo?: string | null;
}

export interface SesionUsuario {
  clienteId: number;
  nombre: string;
  rol: string;
}
