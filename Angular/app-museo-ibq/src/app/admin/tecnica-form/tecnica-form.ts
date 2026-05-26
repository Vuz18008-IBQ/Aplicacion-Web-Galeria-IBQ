import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { Tecnicas } from '../../core/tecnicas';
import { TecnicaRequest } from '../../shared/models/tecnica';

@Component({
  selector: 'app-tecnica-form',
  imports: [ReactiveFormsModule],
  templateUrl: './tecnica-form.html',
  styleUrl: './tecnica-form.css'
})
export class TecnicaForm implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private tecnicasService = inject(Tecnicas);

  cargando = signal(false);
  error = signal<string | null>(null);
  tecnicaId = signal<number | null>(null);
  esEdicion = signal(false);

  form = this.fb.group({
    nombre: ['', Validators.required]
  });

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      const id = Number(idParam);
      this.tecnicaId.set(id);
      this.esEdicion.set(true);
      this.tecnicasService.findById(id).subscribe(tecnica => {
        this.form.patchValue({ nombre: tecnica.nombre });
      });
    }
  }

  cancelar(): void {
    this.router.navigate(['/admin/tecnicas']);
  }

  guardar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.cargando.set(true);
    this.error.set(null);

    const body: TecnicaRequest = { nombre: this.form.getRawValue().nombre! };

    const obs = this.esEdicion()
      ? this.tecnicasService.update(this.tecnicaId()!, body)
      : this.tecnicasService.create(body);

    obs.subscribe({
      next: () => this.router.navigate(['/admin/tecnicas']),
      error: () => {
        this.error.set('No se pudo guardar la técnica');
        this.cargando.set(false);
      }
    });
  }
}
