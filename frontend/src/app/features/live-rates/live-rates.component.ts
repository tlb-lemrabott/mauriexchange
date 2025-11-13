import { Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CurrencyExchangeAPIService } from '../../api/api/currencyExchangeAPI.service';

@Component({
  selector: 'app-live-rates',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './live-rates.component.html',
  styleUrl: './live-rates.component.css'
})
export class LiveRatesComponent {
  private readonly api = inject(CurrencyExchangeAPIService);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rates = signal<any[]>([]);

  constructor() {
    // Fetch available currencies (adjust display mapping as needed)
    this.api.getAllCurrencies().subscribe({
      next: (res: any) => {
        this.rates.set((res?.data as any[]) ?? []);
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
