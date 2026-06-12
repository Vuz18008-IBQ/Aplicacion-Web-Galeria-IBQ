import { Component, computed, HostListener, inject, OnInit, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Obras } from '../../core/obras';
import { Autores } from '../../core/autores';
import { Tecnicas } from '../../core/tecnicas';
import { Materiales } from '../../core/materiales';
import { Obra } from '../../shared/models/obra';
import { Autor } from '../../shared/models/autor';
import { Tecnica } from '../../shared/models/tecnica';
import { Material } from '../../shared/models/material';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-galeria',
  imports: [],
  templateUrl: './galeria.html',
  styleUrl: './galeria.css'
})
export class Galeria implements OnInit {
  private obrasService = inject(Obras);
  private autoresService = inject(Autores);
  private tecnicasService = inject(Tecnicas);
  private materialesService = inject(Materiales);
  private router = inject(Router);
  private apiUrl = environment.apiUrl;
  private porPagina = 15;

  obras = signal<Obra[]>([]);
  autores = signal<Autor[]>([]);
  tecnicas = signal<Tecnica[]>([]);
  materiales = signal<Material[]>([]);
  busqueda = signal('');
  filtroAutor = signal<number | null>(null);
  filtroTecnica = signal<number | null>(null);
  filtroMaterial = signal<number | null>(null);
  pagina = signal(0);
  scrolled = signal(false);

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled.set(window.scrollY > 40);
  }

  obrasFiltradas = computed(() => {
    const term = this.busqueda().toLowerCase().trim();
    const autorId = this.filtroAutor();
    const tecnicaId = this.filtroTecnica();
    const materialId = this.filtroMaterial();

    return this.obras().filter(o => {
      const coincideBusqueda = !term ||
        o.titulo.toLowerCase().includes(term) ||
        (o.autor && `${o.autor.nombre} ${o.autor.apellidos}`.toLowerCase().includes(term));

      const coincideAutor = autorId === null || o.autor?.id === autorId;
      const coincideTecnica = tecnicaId === null || o.tecnica?.id === tecnicaId;
      const coincideMaterial = materialId === null ||
        (o.materiales?.some(m => m.id === materialId) ?? false);

      return coincideBusqueda && coincideAutor && coincideTecnica && coincideMaterial;
    });
  });

  hayFiltros = computed(() =>
    this.filtroAutor() !== null ||
    this.filtroTecnica() !== null ||
    this.filtroMaterial() !== null
  );

  totalPaginas = computed(() =>
    Math.max(1, Math.ceil(this.obrasFiltradas().length / this.porPagina))
  );

  obrasPaginadas = computed(() => {
    const inicio = this.pagina() * this.porPagina;
    return this.obrasFiltradas().slice(inicio, inicio + this.porPagina);
  });

  ngOnInit(): void {
    this.obrasService.findAll().subscribe(obras => this.obras.set(obras));
    this.autoresService.findAll().subscribe(autores => this.autores.set(autores));
    this.tecnicasService.findAll().subscribe(tecnicas => this.tecnicas.set(tecnicas));
    this.materialesService.findAll().subscribe(materiales => this.materiales.set(materiales));
  }

  onBusqueda(event: Event): void {
    const valor = (event.target as HTMLInputElement).value;
    this.busqueda.set(valor);
    this.pagina.set(0);
  }

  onFiltroAutor(event: Event): void {
    const valor = (event.target as HTMLSelectElement).value;
    this.filtroAutor.set(valor ? Number(valor) : null);
    this.pagina.set(0);
  }

  onFiltroTecnica(event: Event): void {
    const valor = (event.target as HTMLSelectElement).value;
    this.filtroTecnica.set(valor ? Number(valor) : null);
    this.pagina.set(0);
  }

  onFiltroMaterial(event: Event): void {
    const valor = (event.target as HTMLSelectElement).value;
    this.filtroMaterial.set(valor ? Number(valor) : null);
    this.pagina.set(0);
  }

  limpiarFiltros(): void {
    this.filtroAutor.set(null);
    this.filtroTecnica.set(null);
    this.filtroMaterial.set(null);
    this.pagina.set(0);
  }

  nombreCompletoAutor(autor: Autor): string {
    return `${autor.nombre} ${autor.apellidos}`.trim();
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
