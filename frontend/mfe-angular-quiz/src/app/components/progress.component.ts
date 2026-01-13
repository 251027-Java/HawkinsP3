import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, ProgressData, WeakArea } from '../services/api.service';

@Component({
  selector: 'app-progress',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="progress-container">
      <div class="header">
        <h1>Your Progress</h1>
        <p>Track your aviation knowledge journey</p>
      </div>

      @if (loading()) {
        <div class="loading">
          <div class="spinner"></div>
          <p>Loading progress...</p>
        </div>
      }

      @if (error()) {
        <div class="error-message">{{ error() }}</div>
      }

      @if (!loading() && progress()) {
        <!-- Stats Overview -->
        <div class="stats-grid">
          <div class="stat-card">
            <div class="stat-icon">📝</div>
            <div class="stat-value">{{ progress()!.totalQuizzesTaken }}</div>
            <div class="stat-label">Quizzes Completed</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">❓</div>
            <div class="stat-value">{{ progress()!.totalQuestionsAttempted }}</div>
            <div class="stat-label">Questions Answered</div>
          </div>
          <div class="stat-card">
            <div class="stat-icon">🎯</div>
            <div class="stat-value">{{ progress()!.overallAccuracy | number:'1.1-1' }}%</div>
            <div class="stat-label">Overall Accuracy</div>
          </div>
        </div>

        <!-- Category Progress -->
        <div class="section">
          <h2>Category Performance</h2>
          <div class="category-grid">
            @for (cat of progress()!.categoryProgress; track cat.categoryName) {
              <div class="category-card">
                <div class="category-header">
                  <span class="category-name">{{ cat.categoryName }}</span>
                  <span class="category-accuracy" [class.good]="cat.accuracy >= 70" [class.bad]="cat.accuracy < 70">
                    {{ cat.accuracy | number:'1.0-0' }}%
                  </span>
                </div>
                <div class="progress-bar">
                  <div class="progress-fill" [style.width.%]="cat.accuracy" [class.good]="cat.accuracy >= 70"></div>
                </div>
                <div class="category-meta">{{ cat.questionCount }} questions attempted</div>
              </div>
            }
          </div>
        </div>

        <!-- Weak Areas -->
        @if (weakAreas().length > 0) {
          <div class="section">
            <h2>📚 Areas to Improve</h2>
            <div class="weak-areas-list">
              @for (area of weakAreas(); track area.categoryName) {
                <div class="weak-area-card">
                  <div class="weak-area-info">
                    <span class="weak-area-name">{{ area.categoryName }}</span>
                    <span class="weak-area-meta">{{ area.questionsAttempted }} questions</span>
                  </div>
                  <span class="weak-area-accuracy">{{ area.accuracyPercentage | number:'1.0-0' }}%</span>
                </div>
              }
            </div>
          </div>
        }
      }

      @if (!loading() && !progress()) {
        <div class="empty-state">
          <span class="icon">📊</span>
          <h3>No Progress Yet</h3>
          <p>Start taking quizzes to track your progress!</p>
          <a href="/quizzes" class="start-btn">Browse Quizzes</a>
        </div>
      }
    </div>
  `,
  styles: [`
    .progress-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 32px 24px;
    }

    .header {
      margin-bottom: 32px;
    }

    .header h1 {
      font-size: 2rem;
      font-weight: 700;
      color: var(--color-text, #1e293b);
      margin-bottom: 8px;
    }

    .header p {
      color: var(--color-text-secondary, #64748b);
    }

    .loading {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 64px;
      color: var(--color-text-secondary, #64748b);
    }

    .spinner {
      width: 48px;
      height: 48px;
      border: 4px solid var(--color-border, #e2e8f0);
      border-top-color: var(--color-primary, #3b82f6);
      border-radius: 50%;
      animation: spin 1s linear infinite;
      margin-bottom: 16px;
    }

    @keyframes spin {
      to { transform: rotate(360deg); }
    }

    .error-message {
      background: rgba(239, 68, 68, 0.1);
      border: 1px solid rgba(239, 68, 68, 0.3);
      border-radius: 8px;
      padding: 16px;
      color: #ef4444;
      margin-bottom: 24px;
    }

    .stats-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      margin-bottom: 40px;
    }

    .stat-card {
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 16px;
      padding: 24px;
      text-align: center;
    }

    .stat-icon {
      font-size: 2rem;
      margin-bottom: 12px;
    }

    .stat-value {
      font-size: 2rem;
      font-weight: 700;
      color: var(--color-text, #1e293b);
      margin-bottom: 4px;
    }

    .stat-label {
      color: var(--color-text-secondary, #64748b);
      font-size: 0.9rem;
    }

    .section {
      margin-bottom: 40px;
    }

    .section h2 {
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--color-text, #1e293b);
      margin-bottom: 20px;
    }

    .category-grid {
      display: grid;
      gap: 16px;
    }

    .category-card {
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 12px;
      padding: 20px;
    }

    .category-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;
    }

    .category-name {
      font-weight: 600;
      color: var(--color-text, #1e293b);
    }

    .category-accuracy {
      font-weight: 600;
      padding: 4px 12px;
      border-radius: 16px;
    }

    .category-accuracy.good {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
    }

    .category-accuracy.bad {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;
    }

    .progress-bar {
      height: 8px;
      background: var(--color-border, #e2e8f0);
      border-radius: 4px;
      overflow: hidden;
      margin-bottom: 8px;
    }

    .progress-fill {
      height: 100%;
      background: #ef4444;
      transition: width 0.5s ease;
    }

    .progress-fill.good {
      background: linear-gradient(90deg, #22c55e, #16a34a);
    }

    .category-meta {
      font-size: 0.85rem;
      color: var(--color-text-secondary, #64748b);
    }

    .weak-areas-list {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .weak-area-card {
      display: flex;
      justify-content: space-between;
      align-items: center;
      background: rgba(239, 68, 68, 0.05);
      border: 1px solid rgba(239, 68, 68, 0.1);
      border-radius: 12px;
      padding: 16px 20px;
    }

    .weak-area-name {
      font-weight: 600;
      color: var(--color-text, #1e293b);
    }

    .weak-area-meta {
      font-size: 0.85rem;
      color: var(--color-text-secondary, #64748b);
      display: block;
    }

    .weak-area-accuracy {
      font-weight: 600;
      color: #ef4444;
    }

    .empty-state {
      text-align: center;
      padding: 64px 32px;
      color: var(--color-text-secondary, #64748b);
    }

    .empty-state .icon {
      font-size: 4rem;
      display: block;
      margin-bottom: 16px;
    }

    .empty-state h3 {
      font-size: 1.5rem;
      color: var(--color-text, #1e293b);
      margin-bottom: 8px;
    }

    .start-btn {
      display: inline-block;
      margin-top: 24px;
      padding: 14px 32px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: white;
      text-decoration: none;
      border-radius: 10px;
      font-weight: 600;
    }
  `]
})
export class ProgressComponent implements OnInit {
  progress = signal<ProgressData | null>(null);
  weakAreas = signal<WeakArea[]>([]);
  loading = signal(true);
  error = signal('');

  constructor(private api: ApiService) { }

  ngOnInit(): void {
    this.loadProgress();
  }

  loadProgress(): void {
    this.loading.set(true);
    this.error.set('');

    // First load analytics for aggregate stats
    this.api.getUserAnalytics().subscribe({
      next: (analytics) => {
        console.log('Analytics data:', analytics);

        // Then load category progress
        this.api.getUserProgress().subscribe({
          next: (categoryData: any) => {
            console.log('Category progress data:', categoryData);

            // Handle different response formats
            let categoryProgress: any[] = [];
            if (Array.isArray(categoryData)) {
              categoryProgress = categoryData.map((cat: any) => ({
                categoryName: cat.categoryName || 'Unknown',
                accuracy: cat.accuracyPercentage || 0,
                questionCount: cat.totalAttempts || 0
              }));
            }

            const progressData: ProgressData = {
              totalQuizzesTaken: analytics?.totalQuizzesTaken || 0,
              totalQuestionsAttempted: analytics?.totalQuestionsAttempted || 0,
              overallAccuracy: analytics?.overallAccuracy || 0,
              categoryProgress: categoryProgress
            };

            this.progress.set(progressData);
            this.loadWeakAreas();
          },
          error: (err) => {
            console.error('Failed to load category progress:', err);
            // Still show analytics even if category progress fails
            const progressData: ProgressData = {
              totalQuizzesTaken: analytics?.totalQuizzesTaken || 0,
              totalQuestionsAttempted: analytics?.totalQuestionsAttempted || 0,
              overallAccuracy: analytics?.overallAccuracy || 0,
              categoryProgress: []
            };
            this.progress.set(progressData);
            this.loading.set(false);
          }
        });
      },
      error: (err) => {
        console.error('Failed to load analytics:', err);
        this.error.set('Failed to load progress');
        this.loading.set(false);
      }
    });
  }

  loadWeakAreas(): void {
    this.api.getWeakAreas().subscribe({
      next: (areas: any[]) => {
        console.log('Weak areas data:', areas);
        // Map to frontend WeakArea format
        const mappedAreas = (areas || []).map((area: any) => ({
          categoryName: area.categoryName || 'Unknown',
          accuracyPercentage: area.accuracyPercentage || 0,
          questionsAttempted: area.totalAttempts || 0
        }));
        this.weakAreas.set(mappedAreas);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
