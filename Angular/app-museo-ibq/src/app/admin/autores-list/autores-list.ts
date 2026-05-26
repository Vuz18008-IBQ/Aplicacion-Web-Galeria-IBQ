import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Auth } from '../../core/auth';
import { Autores } from '../../core/autores';
import { Autor } from '../../shared/models/autor';

@Component({
  selector: 'app-autores-list',
  imports: [FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './autores-list.html',
  styleUrl: './autores-list.css'
})
export class AutoresList implements OnInit {
  private auth = inject(Auth);
  private autoresService = inject(Autores);
  private router = inject(Router);

  autores = signal<Autor[]>([]);
  busqueda = signal('');

  autoresFiltrados = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    if (!term) return this.autores();
    return this.autores().filter(a =>
      `${a.nombre} ${a.apellidos}`.toLowerCase().includes(term)
    );
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.autoresService.findAll().subscribe(autores => this.autores.set(autores));
  }

  irNuevo(): void {
    this.router.navigate(['/admin/autores/nueva']);
  }

  editar(id: number): void {
    this.router.navigate(['/admin/autores', id, 'editar']);
  }

  borrar(id: number): void {
    if (!confirm('¿Borrar este autor?')) return;
    this.autoresService.delete(id).subscribe(() => {
      this.autores.update(arr => arr.filter(a => a.id !== id));
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
