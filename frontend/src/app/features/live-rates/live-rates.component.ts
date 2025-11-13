import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExchangeRatesAPIService } from '../../api/api/exchangeRatesAPI.service';

@Component({
  selector: 'app-live-rates',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './live-rates.component.html',
  styleUrl: './live-rates.component.css'
})
export class LiveRatesComponent {
  private readonly api = inject(ExchangeRatesAPIService);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rates = signal<any[]>([]);
  protected readonly lastDate = signal<string | null>(null);

  constructor() {
    this.api.getLatestRates().subscribe({
      next: (res: any) => {
        // Debug log to verify payload shape during development
        console.debug('[live-rates] latest response', res);
        const items = Array.isArray(res?.data?.data) ? res.data.data : [];
        const date = res?.data?.date ?? null;
        this.lastDate.set(date);
        this.rates.set(items);
        this.loading.set(false);
      },
      error: (e: any) => {
        this.error.set('Failed to load rates');
        this.loading.set(false);
        console.error(e);
      }
    });
  }
}
