import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { Auth } from '../../core/auth';
import { Tecnicas } from '../../core/tecnicas';
import { Tecnica } from '../../shared/models/tecnica';

@Component({
  selector: 'app-tecnicas-list',
  imports: [FormsModule, RouterLink, RouterLinkActive],
  templateUrl: './tecnicas-list.html',
  styleUrl: './tecnicas-list.css'
})
export class TecnicasList implements OnInit {
  private auth = inject(Auth);
  private tecnicasService = inject(Tecnicas);
  private router = inject(Router);

  tecnicas = signal<Tecnica[]>([]);
  busqueda = signal('');

  tecnicasFiltradas = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    if (!term) return this.tecnicas();
    return this.tecnicas().filter(t => t.nombre.toLowerCase().includes(term));
  });

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.tecnicasService.findAll().subscribe(tecnicas => this.tecnicas.set(tecnicas));
  }

  irNueva(): void {
    this.router.navigate(['/admin/tecnicas/nueva']);
  }

  editar(id: number): void {
    this.router.navigate(['/admin/tecnicas', id, 'editar']);
  }

  borrar(id: number): void {
    if (!confirm('¿Borrar esta técnica?')) return;
    this.tecnicasService.delete(id).subscribe(() => {
      this.tecnicas.update(arr => arr.filter(t => t.id !== id));
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
