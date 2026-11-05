import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { Promocion } from '../../core/interfaces/cinerama.interfaces';
import { formatoFechaHora, formatoSoles } from '../../core/services/api';
import { AlertService } from '../../core/services/alert.service';
import { AuthService } from '../../core/services/auth.service';
import { CarritoService } from '../../core/services/carrito.service';
import { PromocionService } from '../../core/services/catalogo.service';
import { CompraService } from '../../core/services/compra.service';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './carrito.component.html',
  styleUrl: './carrito.component.css'
})
export class CarritoComponent {
  private promoService = inject(PromocionService);
  private compraService = inject(CompraService);
  private alertas = inject(AlertService);
  private router = inject(Router);
  readonly auth = inject(AuthService);
  readonly carrito = inject(CarritoService);

  readonly codigo = signal('');
  readonly promocion = signal<Promocion | null>(null);
  readonly buscandoPromo = signal(false);
  readonly metodoPago = signal('TARJETA');
  readonly enviando = signal(false);
  readonly mensajeError = signal<string | null>(null);

  readonly descuento = computed(() => {
    const promo = this.promocion();
    if (!promo) return 0;
    return (this.carrito.totalButacas() * promo.descuento) / 100;
  });

  readonly totalPagar = computed(
    () => this.carrito.totalButacas() - this.descuento() + this.carrito.totalDulceria()
  );

  readonly metodos = [
    { valor: 'TARJETA', texto: 'Tarjeta de crédito / débito', icono: 'bi-credit-card' },
    { valor: 'EFECTIVO', texto: 'Efectivo en taquilla', icono: 'bi-cash-coin' },
    { valor: 'YAPE', texto: 'Yape', icono: 'bi-phone' },
    { valor: 'PLIN', texto: 'Plin', icono: 'bi-phone-vibrate' }
  ];

  soles(valor: number): string {
    return formatoSoles(valor);
  }

  fecha(iso?: string | null): string {
    return formatoFechaHora(iso);
  }

  aplicarPromocion() {
    const codigo = this.codigo().trim().toUpperCase();
    if (!codigo) {
      this.promocion.set(null);
      return;
    }

    this.buscandoPromo.set(true);
    this.promoService.buscarPorCodigo(codigo).subscribe({
      next: (promo) => {
        this.buscandoPromo.set(false);
        if (promo.activo === 0) {
          this.promocion.set(null);
          this.alertas.info('Promoción inactiva', 'Ese código ya no está vigente.');
          return;
        }
        this.promocion.set(promo);
        this.codigo.set(promo.codigo);
        this.alertas.exito('Promoción aplicada', `${promo.descuento}% de descuento en tus butacas.`);
      },
      error: () => {
        this.buscandoPromo.set(false);
        this.promocion.set(null);
        this.alertas.error('Código inválido', 'Verifica el código de la promoción.');
      }
    });
  }

  quitarPromocion() {
    this.promocion.set(null);
    this.codigo.set('');
  }

  pagar() {
    if (this.enviando()) return;

    const sesion = this.auth.usuario();
    if (!sesion) {
      this.router.navigate(['/cuenta/login'], { queryParams: { returnUrl: '/carrito' } });
      return;
    }

    const funcion = this.carrito.funcion();
    const asientos = this.carrito.asientos();
    if (!funcion?.idFuncion || asientos.length === 0) {
      this.alertas.info('Falta elegir butacas', 'Selecciona una función y tus butacas primero.');
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set(null);

    this.compraService
      .procesar({
        clienteId: sesion.clienteId,
        funcionId: funcion.idFuncion,
        asientosIds: asientos.map((a) => a.idAsiento),
        metodoPago: this.metodoPago(),
        productos: this.carrito.itemsDulceria(),
        codigoPromocion: this.promocion()?.codigo ?? null
      })
      .subscribe({
        next: (compras) => {
          this.enviando.set(false);
          const primera = compras[0];
          this.carrito.limpiar();
          this.router.navigate(['/confirmacion', primera.idCompra]);
        },
        error: (err) => {
          this.enviando.set(false);
          const texto =
            err?.error?.message ??
            'No se pudo procesar la compra. Puede que otra persona haya tomado esas butacas.';
          this.mensajeError.set(texto);
          this.alertas.error('Compra no completada', texto);
        }
      });
  }
}
