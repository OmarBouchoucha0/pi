import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

const API_BASE = 'http://localhost:8000';

export interface Prediction {
  month: string;
  predicted_count: number;
  is_surge: boolean;
}

export interface PredictionResponse {
  predictions: Prediction[];
  surge_threshold: number;
  model_version: string;
}

@Injectable({
  providedIn: 'root',
})
export class PredictionService {
  private http = inject(HttpClient);

  getPredictions(nMonths = 6): Observable<PredictionResponse> {
    return this.http.post<PredictionResponse>(`${API_BASE}/api/predict`, {
      n_months: nMonths,
    });
  }

  getHealth(): Observable<{ status: string; model_loaded: boolean }> {
    return this.http.get<{ status: string; model_loaded: boolean }>(`${API_BASE}/api/health`);
  }

  getSurgePredictions(): Observable<{
    surge_predictions: Prediction[];
    surge_threshold: number;
  }> {
    return this.http.get<{
      surge_predictions: Prediction[];
      surge_threshold: number;
    }>(`${API_BASE}/api/surge`);
  }
}
