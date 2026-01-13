import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ApiService, Quiz } from '../../services/api.service';

@Component({
  selector: 'app-quiz-management',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="admin-container">
      <div class="header">
        <div>
            <h1>Quiz Management</h1>
            <p>Create, update, and delete quizzes</p>
        </div>
        <div class="actions">
            <button class="secondary-btn" routerLink="/admin/questions">Manage Question Bank</button>
            <button class="create-btn" routerLink="/admin/quizzes/new">+ Create New Quiz</button>
        </div>
      </div>

      @if (loading()) {
        <div class="loading">Loading quizzes...</div>
      }

      @if (error()) {
        <div class="error">{{ error() }}</div>
      }

      @if (!loading() && quizzes().length === 0) {
        <div class="empty-state">No quizzes found. Create one!</div>
      }

      <div class="quiz-list">
        @for (quiz of quizzes(); track quiz.id) {
          <div class="quiz-item">
            <div class="quiz-info">
              <h3>{{ quiz.title }}</h3>
              <div class="meta">
                <span class="badge category">{{ quiz.categoryName || 'General' }}</span>
                <span class="badge rating" [class]="quiz.ratingType?.toLowerCase() || 'private'">{{ getRatingLabel(quiz.ratingType) }}</span>
                <span class="questions">{{ quiz.questionCount }} questions</span>
              </div>
            </div>
            <div class="actions">
              <a [routerLink]="['/admin/quizzes', quiz.id]" class="edit-btn">Edit</a>
              <button (click)="deleteQuiz(quiz.id)" class="delete-btn">Delete</button>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: [`
    .admin-container {
      max-width: 1000px;
      margin: 0 auto;
      padding: 32px 24px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 32px;
    }

    .actions { display: flex; gap: 12px; }
    
    .create-btn {
      background: #10b981;
      color: white;
      margin: 0;
      color: var(--color-text, #1e293b);
    }
    
    .create-btn {
      background: #10b981;
      color: white;
      border: none;
      padding: 10px 20px;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      text-decoration: none;
    }
    
    .secondary-btn {
      background: white;
      color: #475569;
      border: 1px solid #cbd5e1;
      padding: 10px 20px;
      border-radius: 6px;
      font-weight: 600;
      cursor: pointer;
      text-decoration: none;
      transition: background 0.2s;
    }
    .secondary-btn:hover { background: #f1f5f9; }

    .quiz-item {
      background: white;
      border: 1px solid #e2e8f0;
      border-radius: 8px;
      padding: 20px;
      margin-bottom: 16px;
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .quiz-info h3 {
      margin: 0 0 8px 0;
    }

    .meta {
      display: flex;
      gap: 12px;
      align-items: center;
    }

    .badge {
      font-size: 0.8rem;
      padding: 2px 8px;
      border-radius: 12px;
      background: #f1f5f9;
    }
    
    .badge.easy { background: #dcfce7; color: #166534; }
    .badge.medium { background: #fef9c3; color: #854d0e; }
    .badge.hard { background: #fee2e2; color: #991b1b; }
    
    /* Aviation Pilot Ratings */
    .badge.private { background: #dbeafe; color: #1e40af; }
    .badge.instrument { background: #ede9fe; color: #5b21b6; }
    .badge.commercial { background: #fef3c7; color: #92400e; }
    .badge.atp { background: #fce7f3; color: #9d174d; }

    .actions {
        display: flex;
        gap: 12px;
    }

    .edit-btn {
        background: #3b82f6;
        color: white;
        padding: 8px 16px;
        border-radius: 6px;
        text-decoration: none;
        font-size: 0.9rem;
    }

    .delete-btn {
        background: #ef4444;
        color: white;
        border: none;
        padding: 8px 16px;
        border-radius: 6px;
        cursor: pointer;
        font-size: 0.9rem;
    }
  `]
})
export class QuizManagementComponent implements OnInit {
  quizzes = signal<Quiz[]>([]);
  loading = signal(true);
  error = signal('');

  constructor(private api: ApiService) { }

  ngOnInit() {
    this.loadQuizzes();
  }

  loadQuizzes() {
    this.loading.set(true);
    this.api.getQuizzes().subscribe({
      next: (data) => {
        this.quizzes.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Failed to load quizzes');
        this.loading.set(false);
      }
    });
  }

  deleteQuiz(id: number) {
    if (confirm('Are you sure you want to delete this quiz?')) {
      this.api.deleteQuiz(id).subscribe({
        next: () => {
          this.loadQuizzes();
        },
        error: () => alert('Failed to delete quiz')
      });
    }
  }

  getRatingLabel(ratingType: string | undefined): string {
    const labels: { [key: string]: string } = {
      'PRIVATE': 'Private Pilot',
      'INSTRUMENT': 'Instrument',
      'COMMERCIAL': 'Commercial',
      'ATP': 'ATP'
    };
    return labels[ratingType || 'PRIVATE'] || ratingType || 'Private Pilot';
  }
}
