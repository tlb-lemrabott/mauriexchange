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

  constructor() {
    this.api.getLatestRates().subscribe({
      next: (res: any) => {
        const items = res?.data?.data ?? [];
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
