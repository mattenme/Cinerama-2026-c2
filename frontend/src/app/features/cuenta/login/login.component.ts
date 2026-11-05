import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="contenedor seccion auth">
      <div class="panel caja">
        <span class="etiqueta"><i class="bi bi-person-badge"></i> Mi cuenta</span>
        <h1>Ingresar</h1>
        <p class="subtitulo mb-4">Usa tu DNI y contraseña para comprar tus entradas.</p>

        @if (error()) {
          <div class="aviso mb-3">{{ error() }}</div>
        }

        <form (ngSubmit)="ingresar()">
          <label class="form-label">DNI</label>
          <input class="form-control mb-3" name="dni" [(ngModel)]="dni" required
                 placeholder="Ej: 77889901" autocomplete="username">

          <label class="form-label">Contraseña</label>
          <input class="form-control mb-4" type="password" name="contrasena" [(ngModel)]="contrasena"
                 required placeholder="••••••" autocomplete="current-password">

          <button class="btn-acento ancho" type="submit" [disabled]="cargando()">
            @if (cargando()) {
              <span class="spinner-border spinner-border-sm"></span> Ingresando…
            } @else {
              Entrar
            }
          </button>
        </form>

        <p class="tenue chico mt-3">
          ¿No tienes cuenta? <a routerLink="/cuenta/registro" class="acento">Regístrate aquí</a>
        </p>

        <div class="demo">
          <strong>Cuentas de prueba</strong>
          <span>Cliente: <code>77889901</code> / <code>123456</code></span>
          <span>Administrador: <code>00000001</code> / <code>admin123</code></span>
        </div>
      </div>
    </section>
  `,
  styles: [`
    .auth { display: flex; justify-content: center; padding-top: 46px; }
    .caja { width: 100%; max-width: 420px; padding: 28px; }
    .caja h1 { font-size: 1.8rem; font-weight: 900; margin: 8px 0 4px; }
    .ancho { width: 100%; }
    .chico { font-size: .84rem; }
    .demo {
      margin-top: 20px;
      padding: 12px 14px;
      border: 1px dashed var(--linea);
      border-radius: 10px;
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: .78rem;
      color: var(--muted);
    }
    .demo strong { color: var(--texto); font-size: .8rem; }
    code { color: var(--acento); }
  `]
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private ruta = inject(ActivatedRoute);
  private alertas = inject(AlertService);

  dni = '';
  contrasena = '';
  readonly cargando = signal(false);
  readonly error = signal<string | null>(null);

  ingresar() {
    if (!this.dni.trim() || !this.contrasena) {
      this.error.set('Ingresa tu DNI y contraseña.');
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    this.auth.login(this.dni.trim(), this.contrasena).subscribe({
      next: (r) => {
        this.cargando.set(false);
        if (!r.success) {
          this.error.set(this.auth.mensaje(r) || 'No se pudo iniciar sesión.');
          return;
        }
        this.alertas.exito(`Bienvenido, ${r.nombre}`);
        const destino = this.ruta.snapshot.queryParamMap.get('returnUrl') || '/home';
        this.router.navigateByUrl(destino);
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('No se pudo conectar con el servidor. Verifica que el backend esté encendido.');
      }
    });
  }
}
