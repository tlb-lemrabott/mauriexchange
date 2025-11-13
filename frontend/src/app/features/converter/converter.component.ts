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
    const amt = Number(this.amount());
    if (!isFinite(amt) || amt <= 0) {
      this.error.set('Please enter a valid amount greater than 0');
      return;
    }
    this.loading.set(true);
    this.error.set(null);
    this.convertApi.convert(this.from(), this.to(), amt).subscribe({
      next: async (res: any) => {
        try {
          let payload: any = res;
          if (res instanceof Blob) {
            const txt = await res.text();
            payload = JSON.parse(txt);
          }
          const valueRaw = payload?.data?.convertedAmount ?? payload?.data?.value ?? null;
          const value = valueRaw != null ? Number(valueRaw) : null;
          const rate = payload?.data?.rate != null ? Number(payload.data.rate) : null;
          const date = payload?.data?.date ?? null;
          this.result.set(
            value !== null
              ? `${amt} ${this.from()} = ${value.toFixed(2)} ${this.to()}${rate != null ? ` (rate: ${rate.toFixed(4)})` : ''}${date ? ` • ${date}` : ''}`
              : 'No result'
          );
        } catch (e) {
          console.error(e);
          this.error.set('Failed to parse conversion result');
        } finally {
          this.loading.set(false);
        }
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
