import { Component, ElementRef, ViewChild, inject, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ExchangeRatesAPIService } from '../../api/api/exchangeRatesAPI.service';

declare const Chart: any;

@Component({
  selector: 'app-historical',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './historical.component.html',
  styleUrl: './historical.component.css'
})
export class HistoricalComponent {
  private readonly http = inject(HttpClient);
  private readonly api = inject(ExchangeRatesAPIService);

  @ViewChild('chartCanvas', { static: false }) chartRef?: ElementRef<HTMLCanvasElement>;
  private chart: any;

  protected currency = signal<string>('USD');
  protected range = signal<'30d' | '6m' | '1y'>('30d');
  protected loading = signal<boolean>(false);
  protected error = signal<string | null>(null);
  protected currencies = signal<Array<{ code: string; name?: string }>>([]);

  ngOnInit() {
    // Load currencies from static list for fast UX
    this.http.get<any>('currencies.json').subscribe({
      next: (res) => {
        const raw = Array.isArray(res) ? res : (Array.isArray(res?.data) ? res.data : []);
        const list = raw.map((c: any) => ({ code: c?.code, name: c?.nameFr ?? c?.name ?? c?.code }))
          .filter((c: any) => !!c.code);
        this.currencies.set(list);
      },
      error: () => {}
    });
    // Initial fetch
    this.fetch();
  }

  onCurrencyChange(value: string) {
    this.currency.set(value);
    this.fetch();
  }

  setRange(value: '30d' | '6m' | '1y') {
    this.range.set(value);
    this.fetch();
  }

  private fetch() {
    const code = this.currency();
    const { start, end } = this.computeRange(this.range());
    this.loading.set(true);
    this.error.set(null);
    this.api.getHistory(code, start, end).subscribe({
      next: async (res: any) => {
        try {
          let payload: any = res;
          if (res instanceof Blob) {
            const txt = await res.text();
            payload = JSON.parse(txt);
          }
          const points: Array<{ date: string; officialRate: number }> = Array.isArray(payload?.data)
            ? payload.data
            : Array.isArray(payload?.data?.points)
              ? payload.data.points
              : [];
          const labels = points.map(p => p.date);
          const data = points.map(p => Number(p.officialRate));
          this.renderChart(labels, data, code);
        } catch (e) {
          console.error(e);
          this.error.set('Failed to parse history data');
        } finally {
          this.loading.set(false);
        }
      },
      error: (e: any) => {
        console.error(e);
        this.error.set('Failed to load history');
        this.loading.set(false);
      }
    });
  }

  private renderChart(labels: string[], data: number[], code: string) {
    const ctx = this.chartRef?.nativeElement?.getContext('2d');
    if (!ctx) return;
    if (this.chart) {
      this.chart.destroy();
    }
    this.chart = new Chart(ctx, {
      type: 'line',
      data: {
        labels,
        datasets: [{
          label: `${code}/MRU official rate`,
          data,
          borderColor: '#0b74de',
          backgroundColor: 'rgba(11, 116, 222, 0.1)',
          tension: 0.25,
          pointRadius: 0,
          pointHoverRadius: 4,
          fill: true,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        interaction: { mode: 'index', intersect: false },
        plugins: {
          tooltip: { enabled: true },
          legend: { display: false }
        },
        scales: {
          x: { ticks: { maxTicksLimit: 8 } },
          y: { ticks: { callback: (v: any) => Number(v).toFixed(2) } }
        }
      }
    });
  }

  private computeRange(r: '30d' | '6m' | '1y') {
    const end = new Date();
    const start = new Date();
    const days = r === '30d' ? 30 : r === '6m' ? 182 : 365;
    start.setDate(end.getDate() - days);
    const fmt = (d: Date) => d.toISOString().slice(0, 10);
    return { start: fmt(start), end: fmt(end) };
  }
}
