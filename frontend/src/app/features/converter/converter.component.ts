import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ConversionAPIService } from '../../api/api/conversionAPI.service';
import { CurrencyExchangeAPIService } from '../../api/api/currencyExchangeAPI.service';

@Component({
  selector: 'app-converter',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './converter.component.html',
  styleUrl: './converter.component.css'
})
export class ConverterComponent {
  private readonly convertApi = inject(ConversionAPIService);
  private readonly currencyApi = inject(CurrencyExchangeAPIService);
  protected amount = signal<number>(100);
  protected from = signal<string>('USD');
  protected to = signal<string>('MRU');
  protected result = signal<string>('');
  protected loading = signal(false);
  protected error = signal<string | null>(null);
  protected currencies = signal<Array<{ code: string; name?: string }>>([]);
  protected unitRate = signal<string | null>(null);

  constructor() {
    this.currencyApi.getAllCurrencies().subscribe({
      next: (res: any) => {
        const raw = Array.isArray(res?.data)
          ? res.data
          : Array.isArray(res?.data?.items)
            ? res.data.items
            : [];
        const list: Array<{ code: string; name?: string }> = raw.map((c: any) => ({
          code: c?.code,
          name: c?.nameFr ?? c?.name ?? c?.code,
        })).filter((c: any) => !!c.code);
        this.currencies.set(list);
      },
      error: (e) => {
        console.error(e);
        this.error.set('Failed to load currencies');
      }
    });
    this.updateUnitRate();
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
    if (this.amount() && !this.loading()) {
      this.convert();
    }
    this.updateUnitRate();
  }

  onFromChange(value: string) {
    this.from.set(value);
    this.updateUnitRate();
  }
  onToChange(value: string) {
    this.to.set(value);
    this.updateUnitRate();
  }

  private updateUnitRate() {
    this.convertApi.convert(this.from(), this.to(), 1).subscribe({
      next: (res: any) => {
        const v = (res as any)?.data?.value ?? (res as any)?.data?.amount ?? null;
        this.unitRate.set(v != null ? `1 ${this.from()} = ${v} ${this.to()}` : null);
      },
      error: () => {
        this.unitRate.set(null);
      }
    });
  }
}
