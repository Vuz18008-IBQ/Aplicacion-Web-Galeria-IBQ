import { Component, inject, OnDestroy, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';
import { Autores } from '../../core/autores';
import { Tecnicas } from '../../core/tecnicas';
import { Materiales } from '../../core/materiales';
import { Obras } from '../../core/obras';
import { Imagenes } from '../../core/imagenes';
import { Autor } from '../../shared/models/autor';
import { Tecnica } from '../../shared/models/tecnica';
import { Material } from '../../shared/models/material';
import { Imagen } from '../../shared/models/imagen';
import { ObraRequest } from '../../shared/models/obra';

type ImagenPendiente = {
  file: File;
  previewUrl: string;
};

@Component({
  selector: 'app-obra-form',
  imports: [ReactiveFormsModule],
  templateUrl: './obra-form.html',
  styleUrl: './obra-form.css'
})
export class ObraForm implements OnInit, OnDestroy {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private autoresService = inject(Autores);
  private tecnicasService = inject(Tecnicas);
  private materialesService = inject(Materiales);
  private obrasService = inject(Obras);
  private imagenesService = inject(Imagenes);

  autores = signal<Autor[]>([]);
  tecnicas = signal<Tecnica[]>([]);
  materiales = signal<Material[]>([]);
  materialesSeleccionados = signal<number[]>([]);
  cargando = signal(false);
  error = signal<string | null>(null);
  obraId = signal<number | null>(null);
  esEdicion = signal(false);

  imagenesExistentes = signal<Imagen[]>([]);
  imagenesPendientes = signal<ImagenPendiente[]>([]);

  form = this.fb.group({
    titulo: ['', Validators.required],
    datacion: [''],
    anio: [null as number | null],
    dimensiones: [''],
    tipologia: [''],
    descripcion: [''],
    marcas_inscripciones: [''],
    referencias: [''],
    fecha_ingreso: [''],
    modo_ingreso: [''],
    procedencia: [''],
    estado_conservacion: [''],
    restauraciones: [''],
    ubicacion: [''],
    observaciones: [''],
    autorId: [null as number | null, Validators.required],
    tecnicaId: [null as number | null, Validators.required]
  });

  ngOnInit(): void {
    this.autoresService.findAll().subscribe(autores => this.autores.set(autores));
    this.tecnicasService.findAll().subscribe(tecnicas => this.tecnicas.set(tecnicas));
    this.materialesService.findAll().subscribe(materiales => this.materiales.set(materiales));

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.obraId.set(id);
      this.esEdicion.set(true);
      this.obrasService.findById(id).subscribe(obra => {
        this.form.patchValue({
          titulo: obra.titulo,
          datacion: obra.datacion ?? '',
          anio: obra.anio,
          dimensiones: obra.dimensiones ?? '',
          tipologia: obra.tipologia ?? '',
          descripcion: obra.descripcion ?? '',
          marcas_inscripciones: obra.marcas_inscripciones ?? '',
          referencias: obra.referencias ?? '',
          fecha_ingreso: obra.fecha_ingreso ?? '',
          modo_ingreso: obra.modo_ingreso ?? '',
          procedencia: obra.procedencia ?? '',
          estado_conservacion: obra.estado_conservacion ?? '',
          restauraciones: obra.restauraciones ?? '',
          ubicacion: obra.ubicacion ?? '',
          observaciones: obra.observaciones ?? '',
          autorId: obra.autor?.id ?? null,
          tecnicaId: obra.tecnica?.id ?? null
        });
        if (obra.imagenes) this.imagenesExistentes.set(obra.imagenes);
        if (obra.materiales) this.materialesSeleccionados.set(obra.materiales.map(m => m.id));
      });
    }
  }

  ngOnDestroy(): void {
    this.imagenesPendientes().forEach(p => URL.revokeObjectURL(p.previewUrl));
  }

  onArchivoSeleccionado(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const nuevas: ImagenPendiente[] = Array.from(input.files).map(file => ({
      file,
      previewUrl: URL.createObjectURL(file)
    }));

    this.imagenesPendientes.update(arr => [...arr, ...nuevas]);
    input.value = '';
  }

  quitarPendiente(pendiente: ImagenPendiente): void {
    URL.revokeObjectURL(pendiente.previewUrl);
    this.imagenesPendientes.update(arr => arr.filter(p => p !== pendiente));
  }

  borrarExistente(imagen: Imagen): void {
    if (!confirm('¿Borrar esta imagen?')) return;
    this.imagenesService.delete(imagen.id).subscribe(() => {
      this.imagenesExistentes.update(arr => arr.filter(i => i.id !== imagen.id));
    });
  }

  urlImagen(imagen: Imagen): string {
    return this.imagenesService.fullUrl(imagen);
  }

  toggleMaterial(id: number): void {
    this.materialesSeleccionados.update(arr =>
      arr.includes(id) ? arr.filter(m => m !== id) : [...arr, id]
    );
  }

  esMaterialSeleccionado(id: number): boolean {
    return this.materialesSeleccionados().includes(id);
  }

  cancelar(): void {
    this.router.navigate(['/admin/obras']);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const v = this.form.getRawValue();
    const body: ObraRequest = {
      titulo: v.titulo!,
      datacion: v.datacion || null,
      anio: v.anio,
      dimensiones: v.dimensiones || null,
      tipologia: v.tipologia || null,
      descripcion: v.descripcion || null,
      marcas_inscripciones: v.marcas_inscripciones || null,
      referencias: v.referencias || null,
      fecha_ingreso: v.fecha_ingreso || null,
      modo_ingreso: v.modo_ingreso || null,
      procedencia: v.procedencia || null,
      estado_conservacion: v.estado_conservacion || null,
      restauraciones: v.restauraciones || null,
      ubicacion: v.ubicacion || null,
      observaciones: v.observaciones || null,
      autorId: v.autorId!,
      tecnicaId: v.tecnicaId!,
      materialIds: this.materialesSeleccionados()
    };

    const guardarObs = this.esEdicion()
      ? this.obrasService.update(this.obraId()!, body)
      : this.obrasService.create(body);

    guardarObs.pipe(
      switchMap(obra => {
        const pendientes = this.imagenesPendientes();
        if (pendientes.length === 0) return of(obra);
        const uploads = pendientes.map(p =>
          this.imagenesService.upload(obra.id, p.file, false)
        );
        return forkJoin(uploads).pipe(switchMap(() => of(obra)));
      })
    ).subscribe({
      next: () => this.router.navigate(['/admin/obras']),
      error: () => {
        this.error.set('No se pudo guardar la obra');
        this.cargando.set(false);
      }
    });
  }
}
