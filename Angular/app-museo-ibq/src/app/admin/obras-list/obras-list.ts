import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Auth } from '../../core/auth';
import { Obras } from '../../core/obras';
import { Obra } from '../../shared/models/obra';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-obras-list',
  imports: [FormsModule],
  templateUrl: './obras-list.html',
  styleUrl: './obras-list.css'
})
export class ObrasList implements OnInit {
  private auth = inject(Auth);
  private obrasService = inject(Obras);
  private router = inject(Router);

  private apiUrl = environment.apiUrl;
  private porPagina = 5;

  obras = signal<Obra[]>([]);
  busqueda = signal('');
  pagina = signal(0);

  obrasFiltradas = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    if (!term) return this.obras();
    return this.obras().filter(o =>
      o.titulo.toLowerCase().includes(term) ||
      (o.autor !== null &&
        `${o.autor.nombre} ${o.autor.apellidos}`.toLowerCase().includes(term))
    );
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.obrasFiltradas().length / this.porPagina))
  );

  obrasPaginadas = computed(() => {
    const inicio = this.pagina() * this.porPagina;
    return this.obrasFiltradas().slice(inicio, inicio + this.porPagina);
  });

  inicioRango = computed(() =>
    this.obrasFiltradas().length === 0 ? 0 : this.pagina() * this.porPagina + 1
  );

  finRango = computed(() =>
    Math.min((this.pagina() + 1) * this.porPagina, this.obrasFiltradas().length)
  );

  ngOnInit(): void {
    this.cargar();
  }

  cargar(): void {
    this.obrasService.findAll().subscribe(obras => {
      this.obras.set(obras);
      this.pagina.set(0);
    });
  }

  onBusquedaCambiada(): void {
    this.pagina.set(0);
  }

  paginaAnterior(): void {
    if (this.pagina() > 0) this.pagina.update(p => p - 1);
  }

  paginaSiguiente(): void {
    if (this.pagina() < this.totalPaginas() - 1) this.pagina.update(p => p + 1);
  }

  thumbnailUrl(obra: Obra): string | null {
    if (!obra.imagenes || obra.imagenes.length === 0) return null;
    const principal = obra.imagenes.find(i => i.es_principal) ?? obra.imagenes[0];
    return `${this.apiUrl}${principal.url}`;
  }

  fechaCreacion(obra: Obra): string {
    if (obra.anio !== null) return String(obra.anio);
    if (obra.datacion) return obra.datacion;
    return '—';
  }

  nombreAutor(obra: Obra): string {
    if (!obra.autor) return 'Desconocido';
    return `${obra.autor.nombre} ${obra.autor.apellidos}`.trim();
  }

  irNueva(): void {
    this.router.navigate(['/admin/obras/nueva']);
  }

  editar(id: number): void {
    this.router.navigate(['/admin/obras', id, 'editar']);
  }

  borrar(id: number): void {
    if (!confirm('¿Borrar esta obra?')) return;
    this.obrasService.delete(id).subscribe(() => {
      this.obras.update(arr => arr.filter(o => o.id !== id));
    });
  }

  logout(): void {
    this.auth.logout();
  }
}
