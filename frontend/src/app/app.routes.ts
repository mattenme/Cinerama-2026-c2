import { Routes } from '@angular/router';

import { adminGuard, authGuard } from './core/services/auth.service';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout.component';
import { HomeComponent } from './features/home/home.component';
import { CarteleraComponent } from './features/cartelera/cartelera.component';
import { PeliculaComponent } from './features/pelicula/pelicula.component';
import { DulceriaComponent } from './features/dulceria/dulceria.component';
import { CarritoComponent } from './features/carrito/carrito.component';
import { ConfirmacionComponent } from './features/confirmacion/confirmacion.component';
import { MisComprasComponent } from './features/mis-compras/mis-compras.component';
import { LoginComponent } from './features/cuenta/login/login.component';
import { RegistroComponent } from './features/cuenta/registro/registro.component';
import { AdminPeliculasComponent } from './features/admin/peliculas/admin-peliculas.component';
import { AdminFuncionesComponent } from './features/admin/funciones/admin-funciones.component';
import { AdminProductosComponent } from './features/admin/productos/admin-productos.component';
import { AdminPromocionesComponent } from './features/admin/promociones/admin-promociones.component';

export const routes: Routes = [
  {
    path: '',
    component: MainLayoutComponent,
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'home' },
      { path: 'home', component: HomeComponent },
      { path: 'cartelera', component: CarteleraComponent },
      { path: 'pelicula/:id', component: PeliculaComponent },
      { path: 'dulceria', component: DulceriaComponent },
      { path: 'carrito', component: CarritoComponent },
      { path: 'confirmacion/:id', component: ConfirmacionComponent },
      { path: 'mis-compras', component: MisComprasComponent, canActivate: [authGuard] },
      { path: 'cuenta/login', component: LoginComponent },
      { path: 'cuenta/registro', component: RegistroComponent },
      { path: 'admin/peliculas', component: AdminPeliculasComponent, canActivate: [adminGuard] },
      { path: 'admin/funciones', component: AdminFuncionesComponent, canActivate: [adminGuard] },
      { path: 'admin/productos', component: AdminProductosComponent, canActivate: [adminGuard] },
      { path: 'admin/promociones', component: AdminPromocionesComponent, canActivate: [adminGuard] }
    ]
  },
  { path: '**', redirectTo: 'home' }
];
