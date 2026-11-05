import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({ providedIn: 'root' })
export class AlertService {

  private base = {
    background: '#201e1c',
    color: '#f2eee8',
    confirmButtonColor: '#c43b45'
  };

  exito(titulo: string, texto?: string) {
    return Swal.fire({
      ...this.base,
      icon: 'success',
      title: titulo,
      text: texto,
      timer: 2200,
      showConfirmButton: false
    });
  }

  error(titulo: string, texto?: string) {
    return Swal.fire({
      ...this.base,
      icon: 'error',
      title: titulo,
      text: texto ?? 'Inténtalo nuevamente.'
    });
  }

  info(titulo: string, texto?: string) {
    return Swal.fire({ ...this.base, icon: 'info', title: titulo, text: texto });
  }

  async confirmar(titulo: string, texto: string, boton = 'Sí, continuar'): Promise<boolean> {
    const res = await Swal.fire({
      ...this.base,
      icon: 'warning',
      title: titulo,
      text: texto,
      showCancelButton: true,
      confirmButtonText: boton,
      cancelButtonText: 'Cancelar',
      cancelButtonColor: '#393431'
    });
    return !!res.isConfirmed;
  }
}
