import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Materiales } from '../../core/materiales';
import { MaterialRequest } from '../../shared/models/material';

@Component({
  selector: 'app-material-form',
  imports: [ReactiveFormsModule],
  templateUrl: './material-form.html',
  styleUrl: './material-form.css'
})
export class MaterialForm implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private materialesService = inject(Materiales);

  cargando = signal(false);
  error = signal<string | null>(null);
  materialId = signal<number | null>(null);
  esEdicion = signal(false);

  form = this.fb.group({
    nombre: ['', Validators.required]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.materialId.set(id);
      this.esEdicion.set(true);
      this.materialesService.findById(id).subscribe(material => {
        this.form.patchValue({ nombre: material.nombre });
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/admin/materiales']);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const body: MaterialRequest = { nombre: this.form.getRawValue().nombre! };

    const obs = this.esEdicion()
      ? this.materialesService.update(this.materialId()!, body)
      : this.materialesService.create(body);

    obs.subscribe({
      next: () => this.router.navigate(['/admin/materiales']),
      error: () => {
        this.error.set('No se pudo guardar el material');
        this.cargando.set(false);
      }
    });
  }
}
