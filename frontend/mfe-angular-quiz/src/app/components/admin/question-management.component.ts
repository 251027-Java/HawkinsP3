import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiService, Question } from '../../services/api.service';

@Component({
  selector: 'app-question-management',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, ReactiveFormsModule],
  template: `
    <div class="admin-container">
      <header class="header">
        <h1>Question Bank</h1>
        <div class="actions">
          <button class="primary-btn" (click)="openAddModal()">+ Add New Question</button>
          <button class="template-btn" (click)="downloadTemplate()">⬇ Template</button>
          <div class="upload-wrapper">
            <input type="file" #fileInput (change)="onFileSelected($event)" accept=".csv" style="display: none">
            <button class="upload-btn" (click)="fileInput.click()">⬆ Upload CSV</button>
          </div>
        </div>
      </header>

      <div class="table-container">
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Question</th>
              <th>Category</th>
              <th>Difficulty</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            @for (q of questions(); track q.id) {
              <tr>
                <td>{{ q.id }}</td>
                <td class="question-text" title="{{ q.questionText }}">{{ q.questionText | slice:0:80 }}{{ q.questionText.length > 80 ? '...' : '' }}</td>
                <td>{{ q.categoryName || 'N/A' }}</td>
                <td>
                  <span class="badge" [class]="q.difficulty ? q.difficulty.toLowerCase() : 'medium'">{{ q.difficulty || 'MEDIUM' }}</span>
                </td>
                <td class="actions-cell">
                  <button class="edit-btn" (click)="openEditModal(q)">Edit</button>
                  <button class="delete-btn" (click)="deleteQuestion(q.id)">Delete</button>
                </td>
              </tr>
            }
            @if (questions().length === 0) {
              <tr>
                <td colspan="5" class="empty-state">No questions found. Add one or upload a CSV!</td>
              </tr>
            }
          </tbody>
        </table>
      </div>
      
      <div class="pagination">
        <button [disabled]="currentPage === 0" (click)="changePage(-1)">Previous</button>
        <span>Page {{ currentPage + 1 }}</span>
        <button [disabled]="lastPage" (click)="changePage(1)">Next</button>
      </div>
    </div>

    <!-- Question Modal -->
    @if (showModal()) {
      <div class="modal-overlay">
        <div class="modal-content">
          <h2>{{ isEditing ? 'Edit Question' : 'Add New Question' }}</h2>
          <form [formGroup]="questionForm" (ngSubmit)="saveQuestion()">
            
            <div class="form-group">
              <label>Question Text</label>
              <textarea formControlName="questionText" rows="3"></textarea>
            </div>

            <div class="grid-2">
              <div class="form-group">
                <label>Category Name</label>
                <!-- Simple text input for now, could be a select if we loaded categories -->
                <input formControlName="categoryName" placeholder="e.g. Navigation"> 
              </div>
              <div class="form-group">
                <label>Pilot Rating</label>
                <select formControlName="ratingType">
                  <option value="PRIVATE">Private Pilot</option>
                  <option value="INSTRUMENT">Instrument Rating</option>
                  <option value="COMMERCIAL">Commercial Pilot</option>
                  <option value="ATP">Airline Transport Pilot</option>
                </select>
              </div>
            </div>

            <div class="grid-2">
              <div class="form-group">
                <label>Difficulty</label>
                <select formControlName="difficultyLevel">
                  <option value="EASY">Easy</option>
                  <option value="MEDIUM">Medium</option>
                  <option value="HARD">Hard</option>
                </select>
              </div>
            </div>

            <div class="options-grid">
              <div class="form-group">
                <label>Option A</label>
                <input formControlName="optionA">
              </div>
              <div class="form-group">
                <label>Option B</label>
                <input formControlName="optionB">
              </div>
              <div class="form-group">
                <label>Option C</label>
                <input formControlName="optionC">
              </div>
              <div class="form-group">
                <label>Option D</label>
                <input formControlName="optionD">
              </div>
            </div>

            <div class="form-group">
              <label>Correct Answer</label>
              <select formControlName="correctAnswer">
                <option value="A">Option A</option>
                <option value="B">Option B</option>
                <option value="C">Option C</option>
                <option value="D">Option D</option>
              </select>
            </div>

            <div class="form-group">
              <label>Explanation</label>
              <textarea formControlName="explanation" rows="2"></textarea>
            </div>

            <div class="modal-actions">
              <button type="button" class="cancel-btn" (click)="closeModal()">Cancel</button>
              <button type="submit" class="save-btn" [disabled]="!questionForm.valid">Save Question</button>
            </div>
          </form>
        </div>
      </div>
    }
  `,
  styles: [`
    .admin-container { padding: 32px; max-width: 1200px; margin: 0 auto; }
    .header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 32px; }
    h1 { font-size: 2rem; color: #1e293b; margin: 0; }
    .actions { display: flex; gap: 12px; }
    
    button { padding: 10px 16px; border-radius: 8px; font-weight: 600; cursor: pointer; border: none; transition: transform 0.1s; font-size: 0.9rem; }
    button:disabled { opacity: 0.5; cursor: not-allowed; }
    
    .primary-btn { background: #10b981; color: white; }
    .template-btn { background: white; border: 1px solid #cbd5e1; color: #475569; }
    .upload-btn { background: #3b82f6; color: white; }
    
    .table-container { background: white; border-radius: 12px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); overflow: hidden; margin-bottom: 24px; }
    table { width: 100%; border-collapse: collapse; }
    th { background: #f8fafc; text-align: left; padding: 16px; font-weight: 600; color: #64748b; border-bottom: 1px solid #e2e8f0; }
    td { padding: 16px; border-bottom: 1px solid #e2e8f0; color: #334155; }
    tr:last-child td { border-bottom: none; }
    
    .question-text { max-width: 400px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    
    .badge { padding: 4px 8px; border-radius: 12px; font-size: 0.85rem; font-weight: 500; }
    .badge.easy { background: #dcfce7; color: #166534; }
    .badge.medium { background: #fef9c3; color: #854d0e; }
    .badge.hard { background: #fee2e2; color: #991b1b; }
    
    .actions-cell { display: flex; gap: 8px; }
    .edit-btn { background: none; color: #3b82f6; padding: 4px 8px; border: 1px solid #bfdbfe; }
    .edit-btn:hover { background: #eff6ff; }
    .delete-btn { background: none; color: #ef4444; padding: 4px 8px; }
    .delete-btn:hover { background: #fee2e2; }
    
    .pagination { display: flex; justify-content: center; align-items: center; gap: 16px; }
    
    /* Modal Styles */
    .modal-overlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; justify-content: center; align-items: center; z-index: 1000; }
    .modal-content { background: white; padding: 32px; border-radius: 12px; width: 600px; max-height: 90vh; overflow-y: auto; box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1); }
    .modal-content h2 { margin-top: 0; margin-bottom: 24px; color: #1e293b; }
    
    .form-group { margin-bottom: 16px; display: flex; flex-direction: column; }
    .form-group label { margin-bottom: 6px; font-weight: 500; color: #475569; font-size: 0.9rem; }
    input, textarea, select { padding: 10px; border: 1px solid #cbd5e1; border-radius: 6px; font-size: 0.95rem; }
    input:focus, textarea:focus, select:focus { outline: none; border-color: #3b82f6; ring: 2px solid #bfdbfe; }
    
    .grid-2 { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .options-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 12px; background: #f8fafc; padding: 16px; border-radius: 8px; margin-bottom: 16px; }
    
    .modal-actions { display: flex; justify-content: flex-end; gap: 12px; margin-top: 32px; pt: 16px; border-top: 1px solid #e2e8f0; }
    .cancel-btn { background: white; border: 1px solid #cbd5e1; color: #64748b; }
    .save-btn { background: #3b82f6; color: white; }
  `]
})
export class QuestionManagementComponent implements OnInit {
  questions = signal<Question[]>([]);
  currentPage = 0;
  pageSize = 10;
  lastPage = false;

  // Modal State
  showModal = signal(false);
  isEditing = false;
  editingId: number | null = null;
  questionForm: FormGroup;

  constructor(private api: ApiService, private fb: FormBuilder) {
    this.questionForm = this.fb.group({
      questionText: ['', Validators.required],
      categoryName: ['', Validators.required],
      difficultyLevel: ['MEDIUM', Validators.required],
      ratingType: ['PRIVATE', Validators.required],
      optionA: ['', Validators.required],
      optionB: ['', Validators.required],
      optionC: ['', Validators.required],
      optionD: ['', Validators.required],
      correctAnswer: ['A', Validators.required],
      explanation: ['']
    });
  }

  ngOnInit() {
    this.loadQuestions();
  }

  loadQuestions() {
    this.api.getAllQuestions(this.currentPage, this.pageSize).subscribe({
      next: (response) => {
        this.questions.set(response.content || []);
        this.lastPage = response.last;
      },
      error: (err) => console.error('Error loading questions', err)
    });
  }

  changePage(delta: number) {
    this.currentPage += delta;
    this.loadQuestions();
  }

  downloadTemplate() {
    this.api.downloadQuestionTemplate().subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = 'question_template.csv';
        link.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => alert('Failed to download template')
    });
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      if (!file.name.endsWith('.csv')) {
        alert('Please select a CSV file');
        return;
      }

      this.api.uploadQuestionsCsv(file).subscribe({
        next: (result) => {
          let message = `Upload Complete!\nTotal Rows: ${result.totalRows}\nSuccess: ${result.successCount}\nErrors: ${result.errorCount}`;
          if (result.errorCount > 0) {
            message += `\n\nErrors:\n${result.errors.slice(0, 5).join('\n')}${result.errors.length > 5 ? '\n...' : ''}`;
          }
          alert(message);
          this.loadQuestions();
          event.target.value = '';
        },
        error: (err) => {
          console.error(err);
          alert('Upload failed: ' + (err.error?.message || err.message));
        }
      });
    }
  }

  deleteQuestion(id: number) {
    if (confirm('Are you sure you want to delete this question?')) {
      this.api.deleteQuestion(id).subscribe({
        next: () => this.loadQuestions(),
        error: (err) => alert('Failed to delete question')
      });
    }
  }

  // Modal Handlers
  openAddModal() {
    this.isEditing = false;
    this.editingId = null;
    this.questionForm.reset({
      difficultyLevel: 'MEDIUM',
      correctAnswer: 'A',
      ratingType: 'PRIVATE'
    });
    this.showModal.set(true);
  }

  openEditModal(q: Question) {
    this.isEditing = true;
    this.editingId = q.id;

    const answers = q.answers || [];
    const optionA = answers[0]?.answerText || '';
    const optionB = answers[1]?.answerText || '';
    const optionC = answers[2]?.answerText || '';
    const optionD = answers[3]?.answerText || '';

    // Determine correct answer char (A, B, C, D)
    let correctChar = 'A';
    if (answers[0]?.isCorrect) correctChar = 'A';
    else if (answers[1]?.isCorrect) correctChar = 'B';
    else if (answers[2]?.isCorrect) correctChar = 'C';
    else if (answers[3]?.isCorrect) correctChar = 'D';

    this.questionForm.patchValue({
      questionText: q.questionText,
      categoryName: q.categoryName,
      difficultyLevel: q.difficulty,
      // Default to PRIVATE if undefined, or map from q.ratingType
      ratingType: (q as any).ratingType || 'PRIVATE',
      optionA: optionA,
      optionB: optionB,
      optionC: optionC,
      optionD: optionD,
      correctAnswer: correctChar,
      explanation: q.explanation
    });
    this.showModal.set(true);
  }

  closeModal() {
    this.showModal.set(false);
  }

  saveQuestion() {
    if (this.questionForm.valid) {
      const formVal = this.questionForm.value;

      // Transform Form Data to Backend DTO structure
      const payload: any = {
        questionText: formVal.questionText,
        explanation: formVal.explanation,
        difficulty: formVal.difficultyLevel,
        ratingType: formVal.ratingType,
        categoryName: formVal.categoryName,
        answers: [
          { answerText: formVal.optionA, isCorrect: formVal.correctAnswer === 'A' },
          { answerText: formVal.optionB, isCorrect: formVal.correctAnswer === 'B' },
          { answerText: formVal.optionC, isCorrect: formVal.correctAnswer === 'C' },
          { answerText: formVal.optionD, isCorrect: formVal.correctAnswer === 'D' }
        ]
      };

      if (this.isEditing && this.editingId) {
        this.api.updateQuestion(this.editingId, payload).subscribe({
          next: () => {
            this.loadQuestions();
            this.closeModal();
          },
          error: (err) => alert('Failed to update question: ' + (err.error?.message || 'Unknown error'))
        });
      } else {
        this.api.createQuestion(payload).subscribe({
          next: () => {
            this.loadQuestions();
            this.closeModal();
          },
          error: (err) => alert('Failed to create question: ' + (err.error?.message || 'Unknown error'))
        });
      }
    }
  }
}
