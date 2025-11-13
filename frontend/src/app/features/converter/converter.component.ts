import { Component, inject, signal } from '@angular/core';
import { of, from } from 'rxjs';
import { switchMap } from 'rxjs/operators';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ConversionAPIService } from '../../api/api/conversionAPI.service';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-converter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './converter.component.html',
  styleUrl: './converter.component.css'
})
export class ConverterComponent {
  private readonly convertApi = inject(ConversionAPIService);
  private readonly http = inject(HttpClient);
  protected amount = signal<number>(100);
  protected from = signal<string>('USD');
  protected to = signal<string>('MRU');
  protected result = signal<string>('');
  protected loading = signal(false);
  protected error = signal<string | null>(null);
  protected currencies = signal<Array<{ code: string; name?: string }>>([]);
  protected unitRate = signal<string | null>(null);

  constructor() {
    console.debug('[converter] init: load static currencies.json');
    this.http.get<any>('currencies.json').subscribe({
      next: (res) => {
        const raw = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
        const list: Array<{ code: string; name?: string }> = raw
          .map((c: any) => ({ code: c?.code, name: c?.nameFr ?? c?.name ?? c?.code }))
          .filter((c: any) => !!c.code);
        this.currencies.set(list);
        // Ensure MRU is selectable even if missing
        if (!list.find(c => c.code === 'MRU')) {
          this.currencies.update(arr => [{ code: 'MRU', name: 'Ouguiya mauritanienne' }, ...arr]);
        }
      },
      error: (e) => {
        console.error(e);
        this.error.set('Failed to load currencies list');
      }
    });
  }

  convert() {
    this.loading.set(true);
    this.error.set(null);
    this.convertApi.convert(this.from(), this.to(), this.amount()).subscribe({
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

  swap() {
    const a = this.from();
    const b = this.to();
    this.from.set(b);
    this.to.set(a);
    // do not auto-convert; wait for user to click Convert
  }

  onFromChange(value: string) {
    this.from.set(value);
    // defer conversion until user clicks Convert
  }
  onToChange(value: string) {
    this.to.set(value);
    // defer conversion until user clicks Convert
  }
  // Removed auto unit-rate calls to avoid premature API requests
}
