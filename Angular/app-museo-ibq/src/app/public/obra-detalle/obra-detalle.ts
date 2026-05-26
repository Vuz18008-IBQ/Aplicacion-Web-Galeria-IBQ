import { Component, computed, HostListener, inject, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Obras } from '../../core/obras';
import { Obra } from '../../shared/models/obra';
import { Imagen } from '../../shared/models/imagen';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-obra-detalle',
  imports: [],
  templateUrl: './obra-detalle.html',
  styleUrl: './obra-detalle.css'
})
export class ObraDetalle implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private obrasService = inject(Obras);
  private apiUrl = environment.apiUrl;

  obra = signal<Obra | null>(null);
  cargando = signal(true);
  imagenActual = signal(0);
  scrolled = signal(false);

  @HostListener('window:scroll')
  onScroll(): void {
    this.scrolled.set(window.scrollY > 40);
  }

  imagenes = computed(() => this.obra()?.imagenes ?? []);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.obrasService.findById(id).subscribe({
      next: obra => {
        this.obra.set(obra);
        this.cargando.set(false);
      },
      error: () => this.router.navigate(['/'])
    });
  }

  imagenUrl(imagen: Imagen): string {
    return `${this.apiUrl}${imagen.url}`;
  }

  anterior(): void {
    const n = this.imagenes().length;
    if (n <= 1) return;
    this.imagenActual.update(i => (i === 0 ? n - 1 : i - 1));
  }

  siguiente(): void {
    const n = this.imagenes().length;
    if (n <= 1) return;
    this.imagenActual.update(i => (i === n - 1 ? 0 : i + 1));
  }

  seleccionarImagen(index: number): void {
    this.imagenActual.set(index);
  }

  nombreAutor(): string {
    const autor = this.obra()?.autor;
    if (!autor) return 'Autor desconocido';
    return `${autor.nombre} ${autor.apellidos}`.trim();
  }

  fechaDisplay(): string {
    const obra = this.obra();
    if (!obra) return '';
    if (obra.datacion) return obra.datacion;
    if (obra.anio) return String(obra.anio);
    return '—';
  }

  fechaIngresoDisplay(): string | null {
    const fecha = this.obra()?.fecha_ingreso;
    if (!fecha) return null;
    const d = new Date(fecha);
    if (isNaN(d.getTime())) return null;
    return d.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric'
    });
  }

  volver(): void {
    this.router.navigate(['/']);
  }
}
