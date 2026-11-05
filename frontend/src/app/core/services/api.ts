// El dev server (proxy.config.json) reenvía /api -> http://localhost:8082/cinerama/api
export const API = '/api';

/**
 * Normaliza las rutas de imagen que guarda el backend.
 * Devuelve null cuando no hay imagen usable, para que la vista muestre
 * el póster de respaldo en vez de un icono roto.
 */
export function urlImagen(ruta?: string | null): string | null {
  if (!ruta) return null;
  const valor = ruta.trim();
  if (!valor) return null;

  // Imagen externa (http/https)
  if (/^https?:\/\//i.test(valor)) return valor;

  // Imágenes subidas por el backend: /cinerama/uploads/...
  if (valor.startsWith('/cinerama/uploads/')) return valor;
  if (valor.startsWith('/uploads/')) return `/cinerama${valor}`;

  // Rutas de versiones anteriores del sistema que ya no existen en disco
  return null;
}

/** Precio de la butaca: la misma regla que aplica el backend al procesar la compra. */
export function precioButaca(duracionMinutos?: number | null): number {
  return duracionMinutos && duracionMinutos > 120 ? 25 : 20;
}

export function formatoSoles(valor: number): string {
  return `S/ ${Number(valor ?? 0).toFixed(2)}`;
}

export function formatoFechaHora(iso?: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleString('es-PE', {
    weekday: 'short', day: '2-digit', month: 'short', hour: '2-digit', minute: '2-digit'
  });
}

export function soloFecha(iso?: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleDateString('es-PE', { weekday: 'long', day: '2-digit', month: 'long' });
}

export function soloHora(iso?: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return iso;
  return d.toLocaleTimeString('es-PE', { hour: '2-digit', minute: '2-digit' });
}

/** Convierte la fecha del backend al valor que espera <input type="datetime-local"> */
export function AIsoDatetimeLocal(iso?: string | null): string {
  if (!iso) return '';
  const d = new Date(iso);
  if (isNaN(d.getTime())) return '';
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`;
}
