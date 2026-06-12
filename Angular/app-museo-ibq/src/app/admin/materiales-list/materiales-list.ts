import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Auth } from '../../core/auth';
import { Materiales } from '../../core/materiales';
import { Material } from '../../shared/models/material';

@Component({
  selector: 'app-materiales-list',
  imports: [FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './materiales-list.html',
  styleUrl: './materiales-list.css'
})
export class MaterialesList implements OnInit {
  private auth = inject(Auth);
  private materialesService = inject(Materiales);
  private router = inject(Router);

  materiales = signal<Material[]>([]);
  busqueda = signal('');

  materialesFiltrados = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    if (!term) return this.materiales();
    return this.materiales().filter(m => m.nombre.toLowerCase().includes(term));
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.materialesService.findAll().subscribe(materiales => this.materiales.set(materiales));
  }

  irNuevo(): void {
    this.router.navigate(['/admin/materiales/nuevo']);
  }

  editar(id: number): void {
    this.router.navigate(['/admin/materiales', id, 'editar']);
  }

  borrar(id: number): void {
    if (!confirm('¿Borrar este material?')) return;
    this.materialesService.delete(id).subscribe(() => {
      this.materiales.update(arr => arr.filter(m => m.id !== id));
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
