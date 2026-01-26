import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ApiService, Question, Quiz } from '../services/api.service';

@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="quiz-player">
      @if (loading()) {
        <div class="loading">
          <div class="spinner"></div>
          <p>Loading quiz...</p>
        </div>
      }

      @if (error()) {
        <div class="error-container">
          <div class="error-message">{{ error() }}</div>
          <button (click)="goBack()" class="back-btn">← Back to Quizzes</button>
        </div>
      }

      @if (completed()) {
        <div class="results-container">
          <div class="results-icon">🎉</div>
          <h2>Quiz Completed!</h2>
          <div class="score-display">
            <span class="score">{{ score() }}</span>
            <span class="total">/ {{ questions().length }}</span>
          </div>
          <div class="percentage" [class.good]="percentage() >= 70" [class.bad]="percentage() < 70">
            {{ percentage() }}%
          </div>
          <p class="result-message">
            @if (percentage() >= 90) {
              Excellent work! You've mastered this material! ⭐
            } @else if (percentage() >= 70) {
              Good job! Keep practicing to improve further. 📚
            } @else {
              Keep studying! Review the areas where you struggled. 💪
            }
          </p>
          <div class="result-actions">
            <button (click)="goBack()" class="btn-secondary">Back to Quizzes</button>
            <button (click)="retry()" class="btn-primary">Try Again</button>
          </div>
        </div>
      }

      @if (!loading() && !error() && !completed() && quiz()) {
        <div class="quiz-header">
          <h2>{{ quiz()!.title }}</h2>
          <div class="progress-bar">
            <div class="progress-fill" [style.width.%]="progressPercent()"></div>
          </div>
          <div class="quiz-info">
            <span>Question {{ currentIndex() + 1 }} of {{ questions().length }}</span>
          </div>
        </div>

        @if (currentQuestion()) {
          <div class="question-card">
            <p class="question-text">{{ currentQuestion()!.questionText }}</p>
            
            <div class="options">
              @if (currentQuestion()!.answers && currentQuestion()!.answers!.length > 0) {
                @for (answer of currentQuestion()!.answers; track $index; let i = $index) {
                  <button 
                    (click)="selectAnswer(indexToLetter(i))"
                    [class.selected]="selectedAnswer() === indexToLetter(i)"
                    class="option-btn">
                    <span class="option-letter">{{ indexToLetter(i) }}</span>
                    <span class="option-text">{{ answer.answerText }}</span>
                  </button>
                }
              } @else {
                <button 
                  (click)="selectAnswer('A')"
                  [class.selected]="selectedAnswer() === 'A'"
                  class="option-btn">
                  <span class="option-letter">A</span>
                  <span class="option-text">{{ currentQuestion()!.optionA }}</span>
                </button>
                <button 
                  (click)="selectAnswer('B')"
                  [class.selected]="selectedAnswer() === 'B'"
                  class="option-btn">
                  <span class="option-letter">B</span>
                  <span class="option-text">{{ currentQuestion()!.optionB }}</span>
                </button>
                <button 
                  (click)="selectAnswer('C')"
                  [class.selected]="selectedAnswer() === 'C'"
                  class="option-btn">
                  <span class="option-letter">C</span>
                  <span class="option-text">{{ currentQuestion()!.optionC }}</span>
                </button>
                <button 
                  (click)="selectAnswer('D')"
                  [class.selected]="selectedAnswer() === 'D'"
                  class="option-btn">
                  <span class="option-letter">D</span>
                  <span class="option-text">{{ currentQuestion()!.optionD }}</span>
                </button>
              }
            </div>

            <div class="nav-buttons">
              <button 
                (click)="previousQuestion()" 
                [disabled]="currentIndex() === 0"
                class="nav-btn">
                ← Previous
              </button>
              @if (currentIndex() < questions().length - 1) {
                <button 
                  (click)="nextQuestion()" 
                  [disabled]="!selectedAnswer()"
                  class="nav-btn nav-btn-primary">
                  Next →
                </button>
              } @else {
                <button 
                  (click)="submitQuiz()" 
                  [disabled]="!selectedAnswer()"
                  class="nav-btn nav-btn-submit">
                  Submit Quiz ✓
                </button>
              }
            </div>
          </div>
        }
      }
    </div>
  `,
  styles: [`
    .quiz-player {
      max-width: 800px;
      margin: 0 auto;
      padding: 32px 24px;
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

    .error-container {
      text-align: center;
      padding: 48px;
    }

    .error-message {
      background: rgba(239, 68, 68, 0.1);
      border: 1px solid rgba(239, 68, 68, 0.3);
      border-radius: 8px;
      padding: 16px;
      color: #ef4444;
      margin-bottom: 24px;
    }

    .back-btn {
      padding: 12px 24px;
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 8px;
      cursor: pointer;
    }

    .quiz-header {
      margin-bottom: 32px;
    }

    .quiz-header h2 {
      font-size: 1.5rem;
      font-weight: 700;
      color: var(--color-text, #1e293b);
      margin-bottom: 16px;
    }

    .progress-bar {
      height: 8px;
      background: var(--color-border, #e2e8f0);
      border-radius: 4px;
      overflow: hidden;
      margin-bottom: 12px;
    }

    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #3b82f6, #2563eb);
      transition: width 0.3s ease;
    }

    .quiz-info {
      font-size: 0.9rem;
      color: var(--color-text-secondary, #64748b);
    }

    .question-card {
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 16px;
      padding: 32px;
    }

    .question-text {
      font-size: 1.25rem;
      line-height: 1.6;
      color: var(--color-text, #1e293b);
      margin-bottom: 24px;
    }

    .options {
      display: flex;
      flex-direction: column;
      gap: 12px;
      margin-bottom: 32px;
    }

    .option-btn {
      display: flex;
      align-items: center;
      gap: 16px;
      padding: 16px 20px;
      background: var(--color-background, #f8fafc);
      border: 2px solid var(--color-border, #e2e8f0);
      border-radius: 12px;
      cursor: pointer;
      transition: all 0.2s;
      text-align: left;
    }

    .option-btn:hover {
      border-color: var(--color-primary, #3b82f6);
      background: rgba(59, 130, 246, 0.05);
    }

    .option-btn.selected {
      border-color: var(--color-primary, #3b82f6);
      background: rgba(59, 130, 246, 0.1);
    }

    .option-letter {
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      background: var(--color-border, #e2e8f0);
      border-radius: 8px;
      font-weight: 600;
      color: var(--color-text, #1e293b);
    }

    .option-btn.selected .option-letter {
      background: var(--color-primary, #3b82f6);
      color: white;
    }

    .option-text {
      flex: 1;
      color: var(--color-text, #1e293b);
    }

    .nav-buttons {
      display: flex;
      justify-content: space-between;
      gap: 16px;
    }

    .nav-btn {
      padding: 14px 28px;
      border-radius: 10px;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
      border: 1px solid var(--color-border, #e2e8f0);
      background: var(--color-surface, #ffffff);
      color: var(--color-text, #1e293b);
    }

    .nav-btn:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    .nav-btn-primary {
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: white;
      border: none;
    }

    .nav-btn-submit {
      background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
      color: white;
      border: none;
    }

    /* Results styles */
    .results-container {
      text-align: center;
      padding: 48px;
      background: var(--color-surface, #ffffff);
      border-radius: 16px;
      border: 1px solid var(--color-border, #e2e8f0);
    }

    .results-icon {
      font-size: 4rem;
      margin-bottom: 16px;
    }

    .results-container h2 {
      font-size: 2rem;
      font-weight: 700;
      color: var(--color-text, #1e293b);
      margin-bottom: 24px;
    }

    .score-display {
      font-size: 3rem;
      font-weight: 700;
      margin-bottom: 8px;
    }

    .score {
      color: var(--color-primary, #3b82f6);
    }

    .total {
      color: var(--color-text-secondary, #64748b);
    }

    .percentage {
      font-size: 1.5rem;
      font-weight: 600;
      padding: 8px 24px;
      border-radius: 24px;
      display: inline-block;
      margin-bottom: 24px;
    }

    .percentage.good {
      background: rgba(34, 197, 94, 0.1);
      color: #22c55e;
    }

    .percentage.bad {
      background: rgba(239, 68, 68, 0.1);
      color: #ef4444;
    }

    .result-message {
      color: var(--color-text-secondary, #64748b);
      margin-bottom: 32px;
    }

    .result-actions {
      display: flex;
      gap: 16px;
      justify-content: center;
    }

    .btn-secondary {
      padding: 14px 28px;
      background: var(--color-surface, #ffffff);
      border: 1px solid var(--color-border, #e2e8f0);
      border-radius: 10px;
      cursor: pointer;
      font-weight: 600;
    }

    .btn-primary {
      padding: 14px 28px;
      background: linear-gradient(135deg, #3b82f6 0%, #2563eb 100%);
      color: white;
      border: none;
      border-radius: 10px;
      cursor: pointer;
      font-weight: 600;
    }
  `]
})
export class QuizPlayerComponent implements OnInit {
  quiz = signal<Quiz | null>(null);
  questions = signal<Question[]>([]);
  answers = signal<Map<number, string>>(new Map());
  currentIndex = signal(0);
  selectedAnswer = signal<string>('');
  loading = signal(true);
  error = signal('');
  completed = signal(false);
  score = signal(0);

  currentQuestion = computed(() => this.questions()[this.currentIndex()]);
  progressPercent = computed(() =>
    ((this.currentIndex() + 1) / this.questions().length) * 100
  );
  percentage = computed(() =>
    Math.round((this.score() / this.questions().length) * 100)
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private api: ApiService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const quizId = +params['id'];
      this.loadQuiz(quizId);
    });
  }

  loadQuiz(quizId: number): void {
    this.loading.set(true);
    this.error.set('');

    this.api.getQuizById(quizId).subscribe({
      next: (quiz) => {
        this.quiz.set(quiz);
        this.loadQuestions(quizId);
      },
      error: () => {
        this.error.set('Failed to load quiz');
        this.loading.set(false);
      }
    });
  }

  loadQuestions(quizId: number): void {
    this.api.getQuizQuestions(quizId).subscribe({
      next: (questions) => {
        this.questions.set(questions);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load questions');
        this.loading.set(false);
      }
    });
  }

  selectAnswer(answer: string): void {
    this.selectedAnswer.set(answer);
    const newAnswers = new Map(this.answers());
    newAnswers.set(this.currentQuestion()!.id, answer);
    this.answers.set(newAnswers);
  }

  nextQuestion(): void {
    if (this.currentIndex() < this.questions().length - 1) {
      this.currentIndex.update(i => i + 1);
      const existing = this.answers().get(this.currentQuestion()!.id);
      this.selectedAnswer.set(existing || '');
    }
  }

  previousQuestion(): void {
    if (this.currentIndex() > 0) {
      this.currentIndex.update(i => i - 1);
      const existing = this.answers().get(this.currentQuestion()!.id);
      this.selectedAnswer.set(existing || '');
    }
  }

  submitQuiz(): void {
    // Calculate score
    let correct = 0;
    const responses = this.questions().map(q => {
      const userAnswer = this.answers().get(q.id);

      // Determine if answer is correct
      let isCorrect = false;

      // Check using answers array (from backend QuestionDTO)
      if (q.answers && q.answers.length > 0) {
        const answerIndex = this.letterToIndex(userAnswer);
        if (answerIndex >= 0 && answerIndex < q.answers.length) {
          isCorrect = q.answers[answerIndex].isCorrect === true;
        }
      }
      // Fallback to correctAnswer field if available
      else if (q.correctAnswer) {
        isCorrect = userAnswer === q.correctAnswer;
      }

      if (isCorrect) correct++;

      return {
        questionId: q.id,
        isCorrect: isCorrect,
      };
    });

    this.score.set(correct);
    this.completed.set(true);

    // Submit to backend
    const attempt = {
      quizId: this.quiz()!.id,
      quizTitle: this.quiz()!.title,
      responses: responses,
      timeSpentSeconds: 0 // Would track actual time
    };

    this.api.submitAttempt(attempt).subscribe({
      next: () => console.log('Attempt recorded'),
      error: (err) => console.error('Failed to record attempt', err)
    });
  }

  retry(): void {
    this.answers.set(new Map());
    this.currentIndex.set(0);
    this.selectedAnswer.set('');
    this.completed.set(false);
    this.score.set(0);
  }

  goBack(): void {
    this.router.navigate(['/quizzes']);
  }

  // Convert answer letter (A, B, C, D) to array index (0, 1, 2, 3)
  letterToIndex(letter: string | undefined): number {
    const map: { [key: string]: number } = { 'A': 0, 'B': 1, 'C': 2, 'D': 3 };
    return letter ? (map[letter.toUpperCase()] ?? -1) : -1;
  }

  // Convert array index (0, 1, 2, 3) to answer letter (A, B, C, D)
  indexToLetter(index: number): string {
    const letters = ['A', 'B', 'C', 'D'];
    return letters[index] || 'A';
  }
}
