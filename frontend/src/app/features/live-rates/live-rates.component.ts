import { Component, computed, inject, signal } from '@angular/core';
import { CurrencyExchangeAPIService } from '../../api/api/currencyExchangeAPI.service';
import { provideApi } from '../../api/provide-api';

@Component({
  selector: 'app-live-rates',
  standalone: true,
  providers: [provideApi()],
  templateUrl: './live-rates.component.html',
  styleUrl: './live-rates.component.css'
})
export class LiveRatesComponent {
  private readonly api = inject(CurrencyExchangeAPIService);
  protected readonly loading = signal(true);
  protected readonly error = signal<string | null>(null);
  protected readonly rates = signal<any[]>([]);

  constructor() {
    // Replace with the correct API call once backend endpoint is confirmed
    this.api.listCurrencies().subscribe({
      next: (res) => {
        this.rates.set(res?.data as any[] ?? []);
        this.loading.set(false);
      },
      error: (e) => {
        this.error.set('Failed to load rates');
        this.loading.set(false);
        console.error(e);
      }
    });
  }
}

