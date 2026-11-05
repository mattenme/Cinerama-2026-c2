import { Component, OnInit, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet],
  template: '<router-outlet></router-outlet>'
})
export class AppComponent implements OnInit {
  private auth = inject(AuthService);

  ngOnInit(): void {
    // Confirma con el backend si la cookie de sesión sigue viva
    this.auth.restaurarSesion().subscribe({ error: () => {} });
  }
}
