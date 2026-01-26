import { Routes } from '@angular/router';
import { QuizListComponent } from './components/quiz-list.component';
import { QuizPlayerComponent } from './components/quiz-player.component';
import { ProgressComponent } from './components/progress.component';
import { QuizManagementComponent } from './components/admin/quiz-management.component';
import { QuizEditorComponent } from './components/admin/quiz-editor.component';
import { adminGuard } from './guards/admin.guard';
import { EmptyRouteComponent } from './empty-route/empty-route.component';

export const routes: Routes = [
    { path: 'quizzes', component: QuizListComponent },
    { path: 'quizzes/:id', component: QuizPlayerComponent },
    { path: 'progress', component: ProgressComponent },

    // Admin Routes
    {
        path: 'admin/quizzes',
        component: QuizManagementComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/questions',
        loadComponent: () => import('./components/admin/question-management.component').then(m => m.QuestionManagementComponent),
        canActivate: [adminGuard]
    },
    {
        path: 'admin/quizzes/new',
        component: QuizEditorComponent,
        canActivate: [adminGuard]
    },
    {
        path: 'admin/quizzes/:id',
        component: QuizEditorComponent,
        canActivate: [adminGuard]
    },

    { path: '**', component: EmptyRouteComponent }
];
