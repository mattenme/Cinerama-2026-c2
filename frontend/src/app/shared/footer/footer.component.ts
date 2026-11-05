import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [RouterLink],
  template: `
    <footer class="pie">
      <div class="contenedor pie-interior">
        <div class="pie-marca">
          <span class="pie-logo"><i class="bi bi-film"></i> CINERAMA</span>
          <p>Tu cine de siempre, ahora en un solo lugar. Butacas, dulcería y entradas con QR.</p>
        </div>
        <div class="pie-col">
          <strong>Cartelera</strong>
          <a routerLink="/cartelera">Películas</a>
          <a routerLink="/dulceria">Dulcería</a>
          <a routerLink="/carrito">Mi carrito</a>
        </div>
        <div class="pie-col">
          <strong>Cuenta</strong>
          <a routerLink="/cuenta/login">Ingresar</a>
          <a routerLink="/cuenta/registro">Crear cuenta</a>
          <a routerLink="/mis-compras">Mis compras</a>
        </div>
        <div class="pie-col">
          <strong>Contacto</strong>
          <span><i class="bi bi-geo-alt"></i> Av.?</span>
          <span><i class="bi bi-telephone"></i> (01) 555-0100</span>
          <span><i class="bi bi-envelope"></i> hola&#64;cinerama.pe</span>
        </div>
      </div>
      <div class="contenedor pie-final">
        <span>© 2026 Cinerama</span>
        
      </div>
    </footer>
  `,
  styles: [`
    .pie { background: var(--bg-2); border-top: 1px solid var(--linea); margin-top: 40px; }
    .pie-interior {
      display: grid;
      grid-template-columns: 1.6fr 1fr 1fr 1fr;
      gap: 26px;
      padding: 38px 20px 26px;
    }
    .pie-logo { font-weight: 900; letter-spacing: 2px; color: var(--texto); }
    .pie-logo i { color: var(--acento); }
    .pie-marca p { color: var(--muted); font-size: .86rem; margin: 10px 0 0; max-width: 280px; }
    .pie-col { display: flex; flex-direction: column; gap: 8px; font-size: .86rem; color: var(--muted); }
    .pie-col strong { color: var(--texto); font-size: .82rem; text-transform: uppercase; letter-spacing: 1px; margin-bottom: 2px; }
    .pie-col a { color: var(--muted); }
    .pie-col a:hover { color: var(--acento); }
    .pie-final {
      display: flex;
      justify-content: space-between;
      gap: 12px;
      padding: 16px 20px;
      border-top: 1px solid var(--linea);
      color: var(--muted);
      font-size: .78rem;
      flex-wrap: wrap;
    }
    @media (max-width: 860px) {
      .pie-interior { grid-template-columns: 1fr 1fr; }
    }
  `]
})
export class FooterComponent {}
