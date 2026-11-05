import { HttpClient, HttpInterceptorFn } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Observable, catchError, map, of, tap } from 'rxjs';

import { AuthResponse, SesionUsuario } from '../interfaces/cinerama.interfaces';
import { API } from './api';

/** El backend guarda la sesión en la cookie JSESSIONID: hay que enviarla siempre. */
export const credencialesInterceptor: HttpInterceptorFn = (req, next) =>
  next(req.clone({ withCredentials: true }));

const CLAVE = 'cinerama.sesion';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly usuario = signal<SesionUsuario | null>(this.leerGuardado());
  readonly estaLogueado = computed(() => this.usuario() !== null);
  readonly esAdmin = computed(() => (this.usuario()?.rol ?? '').toUpperCase() === 'ADMIN');

  /** Pregunta al backend si la cookie de sesión sigue activa. */
  restaurarSesion(): Observable<SesionUsuario | null> {
    return this.http.get<AuthResponse>(`${API}/auth/check`).pipe(
      map((r) => {
        if (r.success && r.clienteId) {
          const sesion: SesionUsuario = {
            clienteId: r.clienteId,
            nombre: r.nombre ?? 'Cliente',
            rol: r.rol ?? 'CLIENTE'
          };
          this.guardar(sesion);
          return sesion;
        }
        this.limpiar();
        return null;
      }),
      catchError(() => of(null))
    );
  }

  login(dni: string, contrasena: string): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${API}/auth/login`, { dni, contrasena })
      .pipe(
        tap((r) => {
          if (r.success && r.clienteId) {
            this.guardar({
              clienteId: r.clienteId,
              nombre: r.nombre ?? 'Cliente',
              rol: r.rol ?? 'CLIENTE'
            });
          }
        })
      );
  }

  registrar(datos: {
    dni: string;
    nombre: string;
    email: string;
    telefono?: string;
    contrasena: string;
  }): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/register`, datos);
  }

  verificar(email: string, codigo: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/verificar`, { email, codigo });
  }

  reenviarCodigo(email: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/reenviar-codigo`, { email });
  }

  logout(): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${API}/auth/logout`, {}).pipe(
      tap(() => {
        this.limpiar();
        this.router.navigate(['/home']);
      })
    );
  }

  /** Mensaje de la respuesta: el DTO trae 'message' y el endpoint legado 'mensaje'. */
  mensaje(r: AuthResponse | null | undefined): string {
    return r?.message ?? r?.mensaje ?? '';
  }

  private guardar(sesion: SesionUsuario) {
    this.usuario.set(sesion);
    localStorage.setItem(CLAVE, JSON.stringify(sesion));
  }

  private limpiar() {
    this.usuario.set(null);
    localStorage.removeItem(CLAVE);
  }

  private leerGuardado(): SesionUsuario | null {
    try {
      const txt = localStorage.getItem(CLAVE);
      return txt ? (JSON.parse(txt) as SesionUsuario) : null;
    } catch {
      return null;
    }
  }
}

const destinoLogin = (router: Router, url: string) =>
  router.createUrlTree(['/cuenta/login'], { queryParams: { returnUrl: url } });

// Si el usuario entra directo a una ruta protegida (o recarga la página), el estado
// local puede estar vacío aunque la cookie de sesión siga viva: se consulta al backend
// antes de decidir, para no expulsar a un usuario que sí tiene sesión.
export const authGuard: CanActivateFn = (_ruta, estado) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.estaLogueado()) return true;

  return auth
    .restaurarSesion()
    .pipe(map((usuario) => (usuario ? true : destinoLogin(router, estado.url))));
};

export const adminGuard: CanActivateFn = (_ruta, estado) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const decidir = () => (auth.esAdmin() ? true : destinoLogin(router, estado.url));

  if (auth.estaLogueado()) return decidir();
  return auth.restaurarSesion().pipe(map(() => decidir()));
};
