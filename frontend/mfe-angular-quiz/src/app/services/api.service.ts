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
    difficultyLevel: string;
}

export interface QuizAttempt {
    quizId: number;
    answers: { questionId: number; selectedAnswer: string }[];
    timeSpent: number;
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
    categoryProgress: { categoryName: string; accuracy: number; questionCount: number }[];
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

    getQuizQuestions(quizId: number): Observable<Question[]> {
        return this.http.get<Question[]>(`${this.baseUrl}/api/v1/quizzes/${quizId}/questions`, {
            headers: this.getHeaders()
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
