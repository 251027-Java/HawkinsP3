import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, Quiz } from '../services/api.service';

@Component({
    selector: 'app-quiz-list',
    standalone: true,
    imports: [CommonModule, RouterLink],
    template: `
    <div class="quiz-list-container">
      <div class="header">
        <h1>Available Quizzes</h1>
        <p>Select a quiz to start practicing</p>
      </div>

      @if (loading()) {
        <div class="loading">
          <div class="spinner"></div>
          <p>Loading quizzes...</p>
        </div>
      }

      @if (error()) {
        <div class="error-message">
          {{ error() }}
        </div>
      }

      @if (!loading() && quizzes().length === 0) {
        <div class="empty-state">
          <span class="icon">📚</span>
          <h3>No Quizzes Available</h3>
          <p>Check back later for new quiz content!</p>
        </div>
      }

      <div class="quiz-grid">
        @for (quiz of quizzes(); track quiz.id) {
          <div class="quiz-card">
            <div class="quiz-header">
              <span class="category-badge">{{ quiz.categoryName }}</span>
              <span class="difficulty-badge" [class]="quiz.difficultyLevel.toLowerCase()">
                {{ quiz.difficultyLevel }}
              </span>
            </div>
            <h3>{{ quiz.title }}</h3>
            <p class="description">{{ quiz.description }}</p>
            <div class="quiz-meta">
              <span>📝 {{ quiz.questionCount }} questions</span>
              <span>⏱️ {{ quiz.timeLimit }} min</span>
            </div>
            <a [routerLink]="['/quizzes', quiz.id]" class="start-btn">
              Start Quiz →
            </a>
          </div>
        }
      </div>
    </div>
  `,
    styles: [`
    .quiz-list-container {
      max-width: 1200px;
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
      padding: 48px;
      color: var(--color-text-secondary, #64748b);
    }

    .spinner {
      width: 40px;
      height: 40px;
      border: 3px solid var(--color-border, #e2e8f0);
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

    .quiz-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 24px;
    }

    .quiz-card {
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 12px;
      padding: 24px;
      transition: all 0.2s;
    }

    .quiz-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 24px rgba(0, 0, 0, 0.1);
    }

    .quiz-header {
      display: flex;
      gap: 8px;
      margin-bottom: 16px;
    }

    .category-badge {
      background: rgba(59, 130, 246, 0.1);
      color: var(--color-primary, #3b82f6);
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .difficulty-badge {
      padding: 4px 12px;
      border-radius: 20px;
      font-size: 0.8rem;
      font-weight: 500;
    }

    .difficulty-badge.easy {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
    }

    .difficulty-badge.medium {
      background: rgba(245, 158, 11, 0.1);
      color: #f59e0b;
    }

    .difficulty-badge.hard {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;
    }

    .quiz-card h3 {
      font-size: 1.25rem;
      font-weight: 600;
      color: var(--color-text, #1e293b);
      margin-bottom: 8px;
    }

    .description {
      color: var(--color-text-secondary, #64748b);
      font-size: 0.9rem;
      line-height: 1.5;
      margin-bottom: 16px;
    }

    .quiz-meta {
      display: flex;
      gap: 16px;
      font-size: 0.85rem;
      color: var(--color-text-secondary, #64748b);
      margin-bottom: 16px;
    }

    .start-btn {
      display: inline-block;
      width: 100%;
      padding: 12px 24px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: white;
      text-decoration: none;
      border-radius: 8px;
      font-weight: 600;
      text-align: center;
      transition: all 0.2s;
    }

    .start-btn:hover {
      background: linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%);
      transform: translateY(-1px);
    }
  `]
})
export class QuizListComponent implements OnInit {
    quizzes = signal<Quiz[]>([]);
    loading = signal(true);
    error = signal('');

    constructor(private api: ApiService) { }

    ngOnInit(): void {
        this.loadQuizzes();
    }

    loadQuizzes(): void {
        this.loading.set(true);
        this.error.set('');

        this.api.getQuizzes().subscribe({
            next: (data) => {
                this.quizzes.set(data);
                this.loading.set(false);
            },
            error: (err) => {
                this.error.set('Failed to load quizzes. Please try again.');
                this.loading.set(false);
            }
        });
    }
}
