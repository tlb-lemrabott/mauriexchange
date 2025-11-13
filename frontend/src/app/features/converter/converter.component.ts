import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ConversionAPIService } from '../../api/api/conversionAPI.service';

@Component({
  selector: 'app-converter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './converter.component.html',
  styleUrl: './converter.component.css'
})
export class ConverterComponent {
  private readonly api = inject(ConversionAPIService);
  protected amount = signal(100);
  protected from = signal('USD');
  protected to = signal('MRU');
  protected result = signal<string>('');
  protected loading = signal(false);
  protected error = signal<string | null>(null);

  convert() {
    this.loading.set(true);
    this.error.set(null);
    this.api.convert(this.from(), this.to(), this.amount()).subscribe({
      next: (res: any) => {
        const value = (res as any)?.data?.value ?? (res as any)?.data?.amount ?? null;
        this.result.set(value !== null ? `${this.amount()} ${this.from()} = ${value} ${this.to()}` : 'No result');
        this.loading.set(false);
      },
      error: (e: any) => {
        this.error.set('Conversion failed');
        this.loading.set(false);
        console.error(e);
      }
    });
  }
}
