import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AlertService } from '../../../core/services/alert.service';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-registro',
  standalone: true,
  imports: [FormsModule, RouterLink],
  template: `
    <section class="contenedor seccion auth">
      <div class="panel caja">
        <span class="etiqueta"><i class="bi bi-person-plus"></i> Nueva cuenta</span>
        <h1>{{ paso() === 1 ? 'Crear cuenta' : 'Verifica tu correo' }}</h1>

        @if (error()) {
          <div class="aviso mb-3">{{ error() }}</div>
        }

        @if (paso() === 1) {
          <p class="subtitulo mb-4">Regístrate para comprar entradas y guardar tu historial.</p>

          <form (ngSubmit)="registrar()">
            <div class="fila">
              <div>
                <label class="form-label">DNI</label>
                <input class="form-control mb-3" name="dni" [(ngModel)]="datos.dni" required
                       maxlength="8" inputmode="numeric" pattern="[0-9]{8}" placeholder="8 dígitos">
              </div>
              <div>
                <label class="form-label">Teléfono</label>
                <input class="form-control mb-3" name="telefono" [(ngModel)]="datos.telefono" maxlength="20">
              </div>
            </div>

            <label class="form-label">Nombre completo</label>
            <input class="form-control mb-3" name="nombre" [(ngModel)]="datos.nombre" required maxlength="150">

            <label class="form-label">Correo electrónico</label>
            <input class="form-control mb-3" type="email" name="email" [(ngModel)]="datos.email" required>

            <label class="form-label">Contraseña (mínimo 6 caracteres)</label>
            <input class="form-control mb-4" type="password" name="contrasena" [(ngModel)]="datos.contrasena" required minlength="6">

            <button class="btn-acento ancho" type="submit" [disabled]="cargando()">
              @if (cargando()) {
                <span class="spinner-border spinner-border-sm"></span> Registrando…
              } @else {
                Crear cuenta
              }
            </button>
          </form>

          <p class="tenue chico mt-3">
            ¿Ya tienes cuenta? <a routerLink="/cuenta/login" class="acento">Ingresa</a>
          </p>
        } @else {
          <p class="subtitulo mb-3">
            Enviamos un código de 6 dígitos a <strong class="acento">{{ datos.email }}</strong>.
          </p>

          @if (codigoDesarrollo()) {
            <div class="aviso info mb-3">
              Servidor de correo no configurado. Código de verificación:
              <strong class="acento">{{ codigoDesarrollo() }}</strong>
            </div>
          }

          <label class="form-label">Código de verificación</label>
          <input class="form-control mb-4 codigo" name="codigo" [(ngModel)]="codigo"
                 maxlength="6" placeholder="000000" (keyup.enter)="verificar()">

          <button class="btn-acento ancho" type="button" [disabled]="verificando()" (click)="verificar()">
            @if (verificando()) {
              <span class="spinner-border spinner-border-sm"></span> Verificando…
            } @else {
              Verificar cuenta
            }
          </button>

          <button class="btn-fantasma ancho mt-2" type="button" (click)="reenviar()">
            Reenviar código
          </button>
        }
      </div>
    </section>
  `,
  styles: [`
    .auth { display: flex; justify-content: center; padding-top: 40px; }
    .caja { width: 100%; max-width: 460px; padding: 28px; }
    .caja h1 { font-size: 1.7rem; font-weight: 900; margin: 8px 0 4px; }
    .fila { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; }
    .ancho { width: 100%; }
    .chico { font-size: .84rem; }
    .codigo { letter-spacing: 8px; font-size: 1.15rem; text-align: center; font-weight: 700; }
  `]
})
export class RegistroComponent {
  private auth = inject(AuthService);
  private router = inject(Router);
  private alertas = inject(AlertService);

  datos = { dni: '', nombre: '', email: '', telefono: '', contrasena: '' };
  readonly paso = signal(1);
  readonly cargando = signal(false);
  readonly verificando = signal(false);
  readonly error = signal<string | null>(null);
  readonly codigoDesarrollo = signal<string | null>(null);
  codigo = '';

  registrar() {
    this.error.set(null);

    if (!this.datos.dni.trim() || !this.datos.nombre.trim() || !this.datos.email.trim() || !this.datos.contrasena) {
      this.error.set('Completa los campos obligatorios.');
      return;
    }
    if (!/^\d{8}$/.test(this.datos.dni.trim())) {
      this.error.set('El DNI debe tener exactamente 8 dígitos.');
      return;
    }
    if (this.datos.contrasena.length < 6) {
      this.error.set('La contraseña debe tener al menos 6 caracteres.');
      return;
    }

    this.cargando.set(true);
    this.auth.registrar(this.datos).subscribe({
      next: (r) => {
        this.cargando.set(false);
        if (!r.success) {
          this.error.set(this.auth.mensaje(r) || 'No se pudo registrar la cuenta.');
          return;
        }
        this.codigoDesarrollo.set(r.codigoDesarrollo ?? null);
        if (r.codigoDesarrollo) this.codigo = r.codigoDesarrollo;
        this.paso.set(2);
        this.alertas.exito('Cuenta creada', 'Verifica el código para activar tu cuenta.');
      },
      error: () => {
        this.cargando.set(false);
        this.error.set('No se pudo conectar con el servidor.');
      }
    });
  }

  verificar() {
    if (this.codigo.trim().length !== 6) {
      this.error.set('El código debe tener 6 dígitos.');
      return;
    }

    this.error.set(null);
    this.verificando.set(true);

    this.auth.verificar(this.datos.email.trim(), this.codigo.trim()).subscribe({
      next: (r) => {
        this.verificando.set(false);
        if (!r.success) {
          this.error.set(this.auth.mensaje(r) || 'Código incorrecto.');
          return;
        }
        this.alertas.exito('Cuenta verificada', 'Ya puedes iniciar sesión.');
        this.router.navigate(['/cuenta/login']);
      },
      error: () => {
        this.verificando.set(false);
        this.error.set('No se pudo verificar el código.');
      }
    });
  }

  reenviar() {
    this.auth.reenviarCodigo(this.datos.email.trim()).subscribe({
      next: (r) => {
        if (r.codigoDesarrollo) {
          this.codigoDesarrollo.set(r.codigoDesarrollo);
          this.codigo = r.codigoDesarrollo;
        }
        this.alertas.info('Código reenviado', this.auth.mensaje(r));
      },
      error: () => this.alertas.error('No se pudo reenviar el código')
    });
  }
}
