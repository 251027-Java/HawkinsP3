import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute, RouterLink } from '@angular/router';
import { ApiService, Quiz, Question } from '../../services/api.service';
import { forkJoin, of } from 'rxjs';

@Component({
    selector: 'app-quiz-editor',
    standalone: true,
    imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterLink],
    template: `
    <div class="editor-container">
      <div class="header">
        <h1>{{ isEditing() ? 'Edit Quiz' : 'Create New Quiz' }}</h1>
        <button class="back-btn" routerLink="/admin/quizzes">← Back to Dashboard</button>
      </div>

      <form [formGroup]="quizForm" (ngSubmit)="saveQuiz()">
        <div class="form-section">
          <h2>Quiz Details</h2>
          <div class="form-group">
            <label>Title</label>
            <input formControlName="title" placeholder="e.g. Aerodynamics 101">
          </div>
          <div class="form-group">
            <label>Description</label>
            <textarea formControlName="description" rows="3"></textarea>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>Pilot Rating</label>
              <select formControlName="ratingType">
                <option value="PRIVATE">Private Pilot</option>
                <option value="INSTRUMENT">Instrument Rating</option>
                <option value="COMMERCIAL">Commercial Pilot</option>
                <option value="ATP">Airline Transport Pilot</option>
              </select>
            </div>
            <div class="form-group">
              <label>Time Limit (minutes)</label>
              <input type="number" formControlName="timeLimit">
            </div>
          </div>
        </div>

        @if (isEditing()) {
            <div class="form-section">
                <div class="section-header">
                    <h2>Questions ({{ localQuestions().length }})</h2>
                    <div class="header-actions">
                        @if (hasUnsavedChanges()) {
                            <span class="unsaved-indicator">● Unsaved changes</span>
                        }
                        <button type="button" class="add-btn" (click)="openQuestionPicker()">+ Add from Question Bank</button>
                    </div>
                </div>
                
                <div class="questions-list">
                    @for (q of localQuestions(); track q.id) {
                        <div class="question-item" [class.pending-add]="isPendingAdd(q.id)" [class.pending-remove]="isPendingRemove(q.id)">
                            <div class="q-content">
                                <strong>{{ q.questionText }}</strong>
                                <div class="meta-badges">
                                    <span class="badge rating">{{ q.ratingType }}</span>
                                    <span class="badge difficulty">{{ q.difficulty }}</span>
                                    @if (isPendingAdd(q.id)) {
                                        <span class="badge pending-badge">+ New</span>
                                    }
                                </div>
                            </div>
                            <button type="button" class="remove-btn" (click)="removeQuestionLocally(q.id)">Remove</button>
                        </div>
                    }
                    @if (localQuestions().length === 0) {
                        <div class="empty-state">No questions added yet. Click "Add from Question Bank" to get started.</div>
                    }
                </div>
            </div>
        }

        <div class="form-actions">
           <button type="button" class="cancel-btn" (click)="cancelChanges()">Cancel</button>
           <button type="submit" class="save-main-btn" [disabled]="!quizForm.valid || saving()">
             @if (saving()) {
                 Saving...
             } @else {
                 {{ isEditing() ? 'Save All Changes' : 'Create Quiz' }}
             }
           </button>
        </div>
      </form>
    </div>

    <!-- Question Picker Modal -->
    @if (showQuestionPicker()) {
        <div class="modal-overlay" (click)="closeQuestionPicker()">
            <div class="picker-modal" (click)="$event.stopPropagation()">
                <div class="picker-header">
                    <h2>Select Questions from Bank</h2>
                    <button class="close-btn" (click)="closeQuestionPicker()">✕</button>
                </div>
                
                <div class="picker-filters">
                    <input type="text" placeholder="Search questions..." [(ngModel)]="searchTerm" (input)="applyFilters()">
                    <select [(ngModel)]="filterRating" (change)="applyFilters()">
                        <option value="">All Ratings</option>
                        <option value="PRIVATE">Private Pilot</option>
                        <option value="INSTRUMENT">Instrument</option>
                        <option value="COMMERCIAL">Commercial</option>
                        <option value="ATP">ATP</option>
                    </select>
                    <select [(ngModel)]="filterDifficulty" (change)="applyFilters()">
                        <option value="">All Difficulties</option>
                        <option value="EASY">Easy</option>
                        <option value="MEDIUM">Medium</option>
                        <option value="HARD">Hard</option>
                    </select>
                </div>

                <div class="picker-list">
                    @for (q of filteredAvailableQuestions(); track q.id) {
                        <div class="picker-item" [class.added]="isQuestionInLocalList(q.id)">
                            <div class="q-info">
                                <div class="q-text">{{ q.questionText }}</div>
                                <div class="q-meta">
                                    <span class="badge">{{ q.ratingType }}</span>
                                    <span class="badge">{{ q.difficulty }}</span>
                                    <span class="category">{{ q.categoryName || 'General' }}</span>
                                </div>
                            </div>
                            @if (isQuestionInLocalList(q.id)) {
                                <span class="added-badge">✓ Added</span>
                            } @else {
                                <button class="add-to-quiz-btn" (click)="addQuestionLocally(q)">Add</button>
                            }
                        </div>
                    }
                    @if (filteredAvailableQuestions().length === 0) {
                        <div class="empty-state">No questions match your filters.</div>
                    }
                </div>

                <div class="picker-footer">
                    <span>{{ localQuestions().length }} questions in quiz</span>
                    <button class="done-btn" (click)="closeQuestionPicker()">Done</button>
                </div>
            </div>
        </div>
    }
  `,
    styles: [`
    .editor-container { max-width: 900px; margin: 0 auto; padding: 32px 24px; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
    .back-btn { background: none; border: none; color: #64748b; cursor: pointer; font-size: 1rem; }
    .form-section { background: white; padding: 24px; border-radius: 12px; border: 1px solid #e2e8f0; margin-bottom: 24px; }
    .form-group { margin-bottom: 16px; display: flex; flex-direction: column; }
    .form-group label { margin-bottom: 8px; font-weight: 500; color: #475569; }
    input, textarea, select { padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 1rem; }
    .form-row { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .form-actions { display: flex; gap: 12px; justify-content: flex-end; }
    .cancel-btn { padding: 14px 24px; background: white; color: #64748b; border: 1px solid #cbd5e1; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 1rem; }
    .cancel-btn:hover { background: #f8fafc; }
    .save-main-btn { padding: 14px 24px; background: #3b82f6; color: white; border: none; border-radius: 8px; font-weight: 600; cursor: pointer; font-size: 1rem; }
    .save-main-btn:disabled { background: #94a3b8; cursor: not-allowed; }
    
    .section-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
    .header-actions { display: flex; align-items: center; gap: 16px; }
    .unsaved-indicator { color: #f59e0b; font-weight: 500; font-size: 0.9rem; }
    .add-btn { background: #10b981; color: white; border: none; padding: 8px 16px; border-radius: 6px; cursor: pointer; font-weight: 500; }
    .question-item { border: 1px solid #e2e8f0; padding: 16px; border-radius: 8px; margin-bottom: 12px; display: flex; justify-content: space-between; align-items: start; transition: all 0.2s; }
    .question-item.pending-add { border-color: #86efac; background: #f0fdf4; }
    .q-content strong { display: block; margin-bottom: 8px; }
    .meta-badges { display: flex; gap: 8px; }
    .badge { font-size: 0.75rem; padding: 2px 8px; border-radius: 12px; background: #f1f5f9; color: #475569; }
    .pending-badge { background: #dcfce7; color: #166534; }
    .remove-btn { color: #ef4444; background: none; border: 1px solid #fecaca; padding: 6px 12px; border-radius: 6px; cursor: pointer; font-size: 0.85rem; }
    .remove-btn:hover { background: #fef2f2; }
    .empty-state { text-align: center; color: #94a3b8; padding: 24px; }

    /* Modal Overlay */
    .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
    
    /* Picker Modal */
    .picker-modal { background: white; border-radius: 16px; width: 800px; max-height: 85vh; display: flex; flex-direction: column; }
    .picker-header { display: flex; justify-content: space-between; align-items: center; padding: 20px 24px; border-bottom: 1px solid #e2e8f0; }
    .picker-header h2 { margin: 0; }
    .close-btn { background: none; border: none; font-size: 1.5rem; cursor: pointer; color: #64748b; }
    
    .picker-filters { display: flex; gap: 12px; padding: 16px 24px; border-bottom: 1px solid #e2e8f0; background: #f8fafc; }
    .picker-filters input { flex: 1; }
    .picker-filters select { width: 160px; }
    
    .picker-list { flex: 1; overflow-y: auto; padding: 16px 24px; max-height: 400px; }
    .picker-item { display: flex; justify-content: space-between; align-items: center; padding: 12px 16px; border: 1px solid #e2e8f0; border-radius: 8px; margin-bottom: 8px; transition: all 0.2s; }
    .picker-item:hover { border-color: #cbd5e1; background: #f8fafc; }
    .picker-item.added { background: #f0fdf4; border-color: #bbf7d0; opacity: 0.7; }
    .q-info { flex: 1; }
    .q-text { font-weight: 500; margin-bottom: 6px; }
    .q-meta { display: flex; gap: 8px; font-size: 0.8rem; }
    .category { color: #64748b; }
    .add-to-quiz-btn { background: #3b82f6; color: white; border: none; padding: 6px 16px; border-radius: 6px; cursor: pointer; font-weight: 500; }
    .add-to-quiz-btn:hover { background: #2563eb; }
    .added-badge { color: #16a34a; font-weight: 600; font-size: 0.9rem; }
    
    .picker-footer { display: flex; justify-content: space-between; align-items: center; padding: 16px 24px; border-top: 1px solid #e2e8f0; background: #f8fafc; }
    .done-btn { background: #10b981; color: white; border: none; padding: 10px 24px; border-radius: 8px; cursor: pointer; font-weight: 600; }
  `]
})
export class QuizEditorComponent implements OnInit {
    quizForm: FormGroup;
    isEditing = signal(false);
    categories = signal<any[]>([]);

    // Original questions from server (used to calculate diff)
    originalQuestionIds = signal<Set<number>>(new Set());

    // Local working copy of questions (what user sees and modifies)
    localQuestions = signal<Question[]>([]);

    availableQuestions = signal<Question[]>([]);
    showQuestionPicker = signal(false);
    saving = signal(false);
    currentQuizId: number | null = null;

    // Filters
    searchTerm = '';
    filterRating = '';
    filterDifficulty = '';

    // Computed: Check if there are unsaved question changes
    hasUnsavedChanges = computed(() => {
        const originalIds = this.originalQuestionIds();
        const currentIds = new Set(this.localQuestions().map(q => q.id));

        // Check for additions
        for (const id of currentIds) {
            if (!originalIds.has(id)) return true;
        }
        // Check for removals
        for (const id of originalIds) {
            if (!currentIds.has(id)) return true;
        }
        return false;
    });

    filteredAvailableQuestions = computed(() => {
        let filtered = this.availableQuestions();

        if (this.searchTerm) {
            const term = this.searchTerm.toLowerCase();
            filtered = filtered.filter(q => q.questionText?.toLowerCase().includes(term));
        }
        if (this.filterRating) {
            filtered = filtered.filter(q => q.ratingType === this.filterRating);
        }
        if (this.filterDifficulty) {
            filtered = filtered.filter(q => q.difficulty === this.filterDifficulty);
        }

        return filtered;
    });

    constructor(
        private fb: FormBuilder,
        private api: ApiService,
        private route: ActivatedRoute,
        private router: Router
    ) {
        this.quizForm = this.fb.group({
            title: ['', Validators.required],
            description: ['', Validators.required],
            ratingType: ['PRIVATE', Validators.required],
            timeLimit: [15, Validators.required]
        });
    }

    ngOnInit() {
        this.loadCategories();

        this.route.params.subscribe(params => {
            if (params['id']) {
                this.isEditing.set(true);
                this.currentQuizId = +params['id'];
                this.loadQuiz(this.currentQuizId);
            }
        });
    }

    loadCategories() {
        this.api.getCategories().subscribe(cats => this.categories.set(cats));
    }

    loadQuiz(id: number) {
        this.api.getQuizById(id).subscribe(quiz => {
            this.quizForm.patchValue({
                title: quiz.title,
                description: quiz.description,
                ratingType: quiz.ratingType,
                timeLimit: quiz.timeLimitMinutes || quiz.timeLimit
            });
        });
        this.loadQuestions(id);
    }

    loadQuestions(id: number) {
        this.api.getQuizQuestions(id).subscribe({
            next: (qs) => {
                this.localQuestions.set([...qs]);
                this.originalQuestionIds.set(new Set(qs.map(q => q.id)));
            },
            error: (err) => console.error('Failed to load quiz questions:', err)
        });
    }

    loadAvailableQuestions() {
        this.api.getAllQuestions(0, 200).subscribe({
            next: (response) => {
                const questions = response.content || response;
                this.availableQuestions.set(questions);
            },
            error: (err) => console.error('Failed to load available questions:', err)
        });
    }

    saveQuiz() {
        if (!this.quizForm.valid) return;

        this.saving.set(true);
        const formVal = this.quizForm.value;

        const payload: any = {
            title: formVal.title,
            description: formVal.description,
            ratingType: formVal.ratingType,
            timeLimitMinutes: formVal.timeLimit
        };

        if (this.isEditing() && this.currentQuizId) {
            // First update quiz metadata
            this.api.updateQuiz(this.currentQuizId, payload).subscribe({
                next: () => {
                    console.log('Quiz metadata updated:', this.currentQuizId);
                    this.persistQuestionChanges();
                },
                error: (err) => {
                    console.error('Failed to update quiz:', err);
                    alert('Failed to update quiz: ' + (err.error?.message || err.message || 'Unknown error'));
                    this.saving.set(false);
                }
            });
        } else {
            this.api.createQuiz(payload).subscribe({
                next: (newQuiz) => {
                    console.log('Quiz created successfully:', newQuiz.id);
                    this.saving.set(false);
                    this.router.navigate(['/admin/quizzes', newQuiz.id]);
                },
                error: (err) => {
                    console.error('Failed to create quiz:', err);
                    alert('Failed to create quiz: ' + (err.error?.message || err.message || 'Unknown error'));
                    this.saving.set(false);
                }
            });
        }
    }

    persistQuestionChanges() {
        const originalIds = this.originalQuestionIds();
        const currentIds = new Set(this.localQuestions().map(q => q.id));

        const toAdd: number[] = [];
        const toRemove: number[] = [];

        // Find additions
        for (const id of currentIds) {
            if (!originalIds.has(id)) {
                toAdd.push(id);
            }
        }
        // Find removals
        for (const id of originalIds) {
            if (!currentIds.has(id)) {
                toRemove.push(id);
            }
        }

        console.log('Persisting question changes - Add:', toAdd, 'Remove:', toRemove);

        // Create observables for all operations
        const addOps = toAdd.map(id => this.api.linkQuestionToQuiz(this.currentQuizId!, id));
        const removeOps = toRemove.map(id => this.api.unlinkQuestionFromQuiz(this.currentQuizId!, id));
        const allOps = [...addOps, ...removeOps];

        if (allOps.length === 0) {
            // No question changes, just navigate
            this.saving.set(false);
            this.router.navigate(['/admin/quizzes']);
            return;
        }

        // Execute all operations in parallel
        forkJoin(allOps).subscribe({
            next: () => {
                console.log('All question changes persisted successfully');
                this.saving.set(false);
                this.router.navigate(['/admin/quizzes']);
            },
            error: (err) => {
                console.error('Failed to persist question changes:', err);
                alert('Some question changes may not have been saved. Please check the quiz.');
                this.saving.set(false);
                this.router.navigate(['/admin/quizzes']);
            }
        });
    }

    cancelChanges() {
        if (this.hasUnsavedChanges() && !confirm('You have unsaved changes. Are you sure you want to discard them?')) {
            return;
        }
        this.router.navigate(['/admin/quizzes']);
    }

    // Question Picker Methods
    openQuestionPicker() {
        this.loadAvailableQuestions();
        this.searchTerm = '';
        this.filterRating = '';
        this.filterDifficulty = '';
        this.showQuestionPicker.set(true);
    }

    closeQuestionPicker() {
        this.showQuestionPicker.set(false);
    }

    applyFilters() {
        // Filters are applied via the computed signal, this just triggers reactivity
    }

    // Check if question is in local (pending) list
    isQuestionInLocalList(questionId: number): boolean {
        return this.localQuestions().some(q => q.id === questionId);
    }

    // Check if this is a pending addition (not in original)
    isPendingAdd(questionId: number): boolean {
        return !this.originalQuestionIds().has(questionId);
    }

    // Check if this was marked for removal (in original but not in local) - not used in current UI
    isPendingRemove(questionId: number): boolean {
        return false; // Removed items aren't shown
    }

    // Add question to local list (not persisted until save)
    addQuestionLocally(question: Question) {
        if (this.isQuestionInLocalList(question.id)) return;
        this.localQuestions.update(qs => [...qs, question]);
    }

    // Remove question from local list (not persisted until save)
    removeQuestionLocally(questionId: number) {
        this.localQuestions.update(qs => qs.filter(q => q.id !== questionId));
    }
}
