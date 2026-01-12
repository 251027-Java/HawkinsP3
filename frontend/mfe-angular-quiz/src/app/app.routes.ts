import { Routes } from '@angular/router';
import { QuizListComponent } from './components/quiz-list.component';
import { QuizPlayerComponent } from './components/quiz-player.component';
import { ProgressComponent } from './components/progress.component';

export const routes: Routes = [
    {
        path: '',
        redirectTo: 'quizzes',
        pathMatch: 'full'
    },
    {
        path: 'quizzes',
        component: QuizListComponent
    },
    {
        path: 'quizzes/:id',
        component: QuizPlayerComponent
    },
    {
        path: 'progress',
        component: ProgressComponent
    }
];
