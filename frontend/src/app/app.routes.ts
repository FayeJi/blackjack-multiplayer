import { Routes } from '@angular/router';
import { Login } from './components/login/login';
import { Register } from './components/register/register';
import { Lobby } from './components/lobby/lobby';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';
import { GameTable } from './components/game-table/game-table';
import { Leaderboard } from './components/leaderboard/leaderboard';
import { AdminLayout } from './components/admin/admin-layout/admin-layout';
import { UserList } from './components/admin/user-list/user-list';


export const routes: Routes = [
    { path: 'login', component: Login },
    { path: 'register', component: Register },
    { path: 'lobby', component: Lobby, canActivate: [authGuard] },
    { path: 'leaderboard', component: Leaderboard, canActivate: [authGuard] },
    { path: 'game/:id', component: GameTable, canActivate: [authGuard] },

    // --- ADMIN ROUTES ---
    {
        path: 'admin',
        component: AdminLayout,
        canActivate: [adminGuard],
        children: [
            { path: 'users', component: UserList },
            { path: '', redirectTo: 'users', pathMatch: 'full' }
        ]
    },

    { path: '', redirectTo: '/login', pathMatch: 'full' }, // Default route
    { path: '**', redirectTo: '/login' }
];
