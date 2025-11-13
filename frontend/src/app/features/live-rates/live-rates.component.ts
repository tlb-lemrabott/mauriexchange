import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ExchangeRatesAPIService } from '../../api/api/exchangeRatesAPI.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

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
    this.api
      .getLatestRates()
      .pipe(
        map((res: any) => {
          console.debug('[live-rates] latest response', res);
          const date = res?.data?.date ?? null;
          const items = Array.isArray(res?.data?.data)
            ? res.data.data
            : Array.isArray(res?.data)
              ? res.data
              : [];
          return { date, items };
        }),
        catchError((e) => {
          console.error(e);
          this.error.set('Failed to load rates');
          this.loading.set(false);
          return of({ date: null, items: [] as any[] });
        })
      )
      .subscribe(({ date, items }) => {
        this.lastDate.set(date);
        this.rates.set(items);
        this.loading.set(false);
      });
  }
}
