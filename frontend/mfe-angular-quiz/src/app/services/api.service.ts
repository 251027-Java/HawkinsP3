import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

// API base URL - points to the API Gateway
const API_BASE_URL = 'http://localhost:8888';

export interface Quiz {
    id: number;
    title: string;
    description: string;
    categoryId: number;
    categoryName: string;
    difficultyLevel: string;
    timeLimit: number;
    timeLimitMinutes?: number;
    ratingType?: string;
    questionCount: number;
}

export interface Question {
    id: number;
    questionText: string;
    optionA: string;
    optionB: string;
    optionC: string;
    optionD: string;
    correctAnswer?: string;
    explanation?: string;
    categoryName: string;
    difficulty: string;
    ratingType?: string;
    answers?: { answerText: string; isCorrect: boolean }[];
}

export interface QuestionResponse {
    questionId: number;
    selectedAnswerId?: number;
    isCorrect: boolean;
    categoryId?: number;
}

export interface QuizAttempt {
    quizId: number;
    quizTitle?: string;
    responses: QuestionResponse[];
    timeSpentSeconds: number;
}

export interface AttemptResult {
    id: number;
    quizId: number;
    quizTitle: string;
    score: number;
    totalQuestions: number;
    percentage: number;
    completedAt: string;
}

export interface ProgressData {
    totalQuizzesTaken: number;
    totalQuestionsAttempted: number;
    overallAccuracy: number;
    categoryProgress: CategoryProgress[];
}

export interface CategoryProgress {
    categoryName: string;
    accuracy: number;
    questionCount: number;
}

export interface WeakArea {
    categoryName: string;
    accuracyPercentage: number;
    questionsAttempted: number;
}

@Injectable({
    providedIn: 'root'
})
export class ApiService {
    private baseUrl = API_BASE_URL;

    constructor(private http: HttpClient) { }

    private getHeaders(): HttpHeaders {
        const token = localStorage.getItem('pilotquiz_token');
        let headers = new HttpHeaders({
            'Content-Type': 'application/json'
        });
        if (token) {
            headers = headers.set('Authorization', `Bearer ${token}`);
        }

        const userStr = localStorage.getItem('pilotquiz_user');
        if (userStr) {
            try {
                const user = JSON.parse(userStr);
                if (user.role) {
                    headers = headers.set('X-User-Role', user.role);
                }
            } catch (e) {
                // Ignore parse error
            }
        }
        return headers;
    }

    private getUserId(): string | null {
        const user = localStorage.getItem('pilotquiz_user');
        if (user) {
            try {
                return JSON.parse(user).id;
            } catch {
                return null;
            }
        }
        return null;
    }

    // Quiz endpoints
    getQuizzes(): Observable<Quiz[]> {
        return this.http.get<any>(`${this.baseUrl}/api/v1/quizzes`, {
            headers: this.getHeaders()
        }).pipe(
            map(response => response.content || []),
            catchError(this.handleError)
        );
    }

    getQuizById(id: number): Observable<Quiz> {
        return this.http.get<Quiz>(`${this.baseUrl}/api/v1/quizzes/${id}`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    createQuiz(quiz: Partial<Quiz>): Observable<Quiz> {
        return this.http.post<Quiz>(`${this.baseUrl}/api/v1/quizzes`, quiz, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    updateQuiz(id: number, quiz: Partial<Quiz>): Observable<Quiz> {
        return this.http.put<Quiz>(`${this.baseUrl}/api/v1/quizzes/${id}`, quiz, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    deleteQuiz(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/api/v1/quizzes/${id}`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getQuizQuestions(quizId: number): Observable<Question[]> {
        return this.http.get<Question[]>(`${this.baseUrl}/api/v1/quizzes/${quizId}/questions`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    createQuestion(question: Partial<Question>): Observable<Question> {
        return this.http.post<Question>(`${this.baseUrl}/api/v1/questions`, question, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    addQuestion(quizId: number, question: Partial<Question>): Observable<Question> {
        return this.http.post<Question>(`${this.baseUrl}/api/v1/quizzes/${quizId}/questions`, question, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    linkQuestionToQuiz(quizId: number, questionId: number): Observable<void> {
        return this.http.post<void>(`${this.baseUrl}/api/v1/quizzes/${quizId}/questions/${questionId}`, {}, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    unlinkQuestionFromQuiz(quizId: number, questionId: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/api/v1/quizzes/${quizId}/questions/${questionId}`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    updateQuestion(id: number, question: Partial<Question>): Observable<Question> {
        return this.http.put<Question>(`${this.baseUrl}/api/v1/questions/${id}`, question, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    deleteQuestion(id: number): Observable<void> {
        return this.http.delete<void>(`${this.baseUrl}/api/v1/questions/${id}`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getAllQuestions(page: number = 0, size: number = 10, sort: string = 'id,desc'): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/api/v1/questions?page=${page}&size=${size}&sort=${sort}`, {
            headers: this.getHeaders()
        }).pipe(
            map(response => response),
            catchError(this.handleError)
        );
    }

    uploadQuestionsCsv(file: File): Observable<any> {
        const formData = new FormData();
        formData.append('file', file);

        // Don't set Content-Type header, Angular sets it with boundary automatically for FormData
        const headers = this.getHeaders().delete('Content-Type');

        return this.http.post<any>(`${this.baseUrl}/api/v1/questions/bulk`, formData, {
            headers: headers
        }).pipe(catchError(this.handleError));
    }

    downloadQuestionTemplate(): Observable<Blob> {
        return this.http.get(`${this.baseUrl}/api/v1/questions/template`, {
            headers: this.getHeaders(),
            responseType: 'blob'
        }).pipe(catchError(this.handleError));
    }

    getCategories(): Observable<{ id: number; name: string; description: string }[]> {
        return this.http.get<any[]>(`${this.baseUrl}/api/v1/categories`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    // Progress endpoints
    submitAttempt(attempt: QuizAttempt): Observable<AttemptResult> {
        return this.http.post<AttemptResult>(`${this.baseUrl}/api/v1/attempts`, attempt, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getUserProgress(): Observable<ProgressData> {
        return this.http.get<ProgressData>(`${this.baseUrl}/api/v1/progress`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getUserAnalytics(): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/api/v1/analytics`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getWeakAreas(): Observable<WeakArea[]> {
        return this.http.get<WeakArea[]>(`${this.baseUrl}/api/v1/weak-areas`, {
            headers: this.getHeaders()
        }).pipe(catchError(this.handleError));
    }

    getRecentAttempts(page: number = 0, size: number = 10): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/api/v1/attempts?page=${page}&size=${size}`, {
            headers: this.getHeaders()
        }).pipe(
            map(response => response.content || []),
            catchError(this.handleError)
        );
    }

    private handleError(error: any): Observable<never> {
        console.error('API Error:', error);
        if (error.status === 401) {
            localStorage.removeItem('pilotquiz_token');
            localStorage.removeItem('pilotquiz_user');
            window.dispatchEvent(new CustomEvent('pilotquiz:logout'));
        }
        return throwError(() => error);
    }
}
