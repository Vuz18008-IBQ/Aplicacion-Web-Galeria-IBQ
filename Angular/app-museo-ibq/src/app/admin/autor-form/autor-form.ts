import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Autores } from '../../core/autores';
import { AutorRequest } from '../../shared/models/autor';

@Component({
  selector: 'app-autor-form',
  imports: [ReactiveFormsModule],
  templateUrl: './autor-form.html',
  styleUrl: './autor-form.css'
})
export class AutorForm implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private autoresService = inject(Autores);

  cargando = signal(false);
  error = signal<string | null>(null);
  autorId = signal<number | null>(null);
  esEdicion = signal(false);

  form = this.fb.group({
    nombre: ['', Validators.required],
    apellidos: ['', Validators.required],
    fecha_nacimiento: [null as number | null],
    fecha_muerte: [null as number | null],
    corriente_artistica: [''],
    lugar_nacimiento: ['']
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.autorId.set(id);
      this.esEdicion.set(true);
      this.autoresService.findById(id).subscribe(autor => {
        this.form.patchValue({
          nombre: autor.nombre,
          apellidos: autor.apellidos,
          fecha_nacimiento: autor.fecha_nacimiento,
          fecha_muerte: autor.fecha_muerte,
          corriente_artistica: autor.corriente_artistica ?? '',
          lugar_nacimiento: autor.lugar_nacimiento ?? ''
        });
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/admin/autores']);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const v = this.form.getRawValue();
    const body: AutorRequest = {
      nombre: v.nombre!,
      apellidos: v.apellidos!,
      fecha_nacimiento: v.fecha_nacimiento,
      fecha_muerte: v.fecha_muerte,
      corriente_artistica: v.corriente_artistica || null,
      lugar_nacimiento: v.lugar_nacimiento || null
    };

    const obs = this.esEdicion()
      ? this.autoresService.update(this.autorId()!, body)
      : this.autoresService.create(body);

    obs.subscribe({
      next: () => this.router.navigate(['/admin/autores']),
      error: () => {
        this.error.set('No se pudo guardar el autor');
        this.cargando.set(false);
      }
    });
  }
}
