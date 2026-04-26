import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ChartModule } from 'primeng/chart';
import { ToastModule } from 'primeng/toast';
import { MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { SkeletonModule } from 'primeng/skeleton';
import {
  PredictionService,
  Prediction,
  PredictionResponse,
} from '../../../core/services/prediction.service';

@Component({
  selector: 'app-admin-predictions',
  imports: [CommonModule, ChartModule, ToastModule, ButtonModule, SkeletonModule],
  providers: [MessageService],
  templateUrl: './predictions.component.html',
  styleUrl: './predictions.component.scss',
})
export class PredictionsComponent implements OnInit {
  private predictionService = inject(PredictionService);
  private messageService = inject(MessageService);

  predictions: Prediction[] = [];
  surgeThreshold = 0;
  loading = true;
  chartData: unknown;
  chartOptions: unknown;

  ngOnInit(): void {
    this.loadPredictions();
    this.initChart();
  }

  loadPredictions(): void {
    this.loading = true;
    this.predictionService.getPredictions(6).subscribe({
      next: (response: PredictionResponse) => {
        this.predictions = response.predictions;
        this.surgeThreshold = response.surge_threshold;
        this.loading = false;
        this.updateChartData();
      },
      error: () => {
        this.messageService.add({
          severity: 'error',
          summary: 'Error',
          detail: 'Failed to load predictions',
        });
        this.loading = false;
      },
    });
  }

  initChart(): void {
    const textColor = '#0a0a0a';
    const textColorSecondary = '#6b7280';
    const surfaceBorder = '#e5e7eb';

    this.chartOptions = {
      plugins: {
        legend: {
          labels: {
            color: textColor,
          },
        },
      },
      scales: {
        x: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
          },
        },
        y: {
          ticks: {
            color: textColorSecondary,
          },
          grid: {
            color: surfaceBorder,
          },
        },
      },
    };
  }

  updateChartData(): void {
    const labels = this.predictions.map((p) => p.month);
    const counts = this.predictions.map((p) => p.predicted_count);
    const surgeColors = this.predictions.map((p) => (p.is_surge ? '#ef4444' : '#0a0a0a'));

    this.chartData = {
      labels,
      datasets: [
        {
          label: 'Predicted Registrations',
          data: counts,
          backgroundColor: surgeColors,
          borderColor: surgeColors,
          fill: false,
        },
        {
          label: 'Surge Threshold',
          data: Array(this.predictions.length).fill(this.surgeThreshold),
          borderColor: '#ef4444',
          borderDash: [5, 5],
          fill: false,
          pointRadius: 0,
        },
      ],
    };
  }

  get surgeCount(): number {
    return this.predictions.filter((p) => p.is_surge).length;
  }

  get avgPredictedCount(): number {
    if (this.predictions.length === 0) return 0;
    const sum = this.predictions.reduce((acc, p) => acc + p.predicted_count, 0);
    return Math.round(sum / this.predictions.length);
  }
}
