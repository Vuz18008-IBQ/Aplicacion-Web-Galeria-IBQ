import { Component, computed, HostListener, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Obras } from '../../core/obras';
import { Obra } from '../../shared/models/obra';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-galeria',
  imports: [],
  templateUrl: './galeria.html',
  styleUrl: './galeria.css'
})
export class Galeria implements OnInit {
  private obrasService = inject(Obras);
  private router = inject(Router);
  private apiUrl = environment.apiUrl;
  private porPagina = 15;

  obras = signal<Obra[]>([]);
  busqueda = signal('');
  pagina = signal(0);
  scrolled = signal(false);

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled.set(window.scrollY > 40);
  }

  obrasFiltradas = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    if (!term) return this.obras();
    return this.obras().filter(o =>
      o.titulo.toLowerCase().includes(term) ||
      (o.autor && `${o.autor.nombre} ${o.autor.apellidos}`.toLowerCase().includes(term))
    );
  });

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.obrasFiltradas().length / this.porPagina))
  );

  obrasPaginadas = computed(() => {
    const inicio = this.pagina() * this.porPagina;
    return this.obrasFiltradas().slice(inicio, inicio + this.porPagina);
  });

  ngOnInit(): void {
    this.obrasService.findAll().subscribe(obras => this.obras.set(obras));
  }

  onBusqueda(event: Event): void {
    const valor = (event.target as HTMLInputElement).value;
    this.busqueda.set(valor);
    this.pagina.set(0);
  }

  thumbnailUrl(obra: Obra): string | null {
    if (!obra.imagenes || obra.imagenes.length === 0) return null;
    const principal = obra.imagenes.find(i => i.es_principal) ?? obra.imagenes[0];
    return `${this.apiUrl}${principal.url}`;
  }

  nombreAutor(obra: Obra): string {
    if (!obra.autor) return 'Autor desconocido';
    return `${obra.autor.nombre} ${obra.autor.apellidos}`.trim();
  }

  fechaCard(obra: Obra): string | null {
    return obra.datacion || obra.anio?.toString() || null;
  }

  verObra(id: number): void {
    this.router.navigate(['/obra', id]);
  }

  paginaAnterior(): void {
    if (this.pagina() > 0) this.pagina.update(p => p - 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  paginaSiguiente(): void {
    if (this.pagina() < this.totalPaginas() - 1) this.pagina.update(p => p + 1);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
