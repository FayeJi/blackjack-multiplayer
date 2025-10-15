import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';
import { UserStats } from '../../services/user';
import { CardModule } from 'primeng/card';
import { TableModule } from 'primeng/table';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';
import { AvatarModule } from 'primeng/avatar';
import { ProgressBarModule } from 'primeng/progressbar';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ToolbarModule } from 'primeng/toolbar';

@Component({
    selector: 'app-leaderboard',
    standalone: true,
    imports: [CommonModule, RouterModule, CardModule,ToolbarModule,
        TableModule,
        ButtonModule,
        RippleModule,
        AvatarModule,
        ProgressBarModule,
        ProgressSpinnerModule],
    templateUrl: './leaderboard.html',
    styleUrls: ['./leaderboard.scss']
})
export class Leaderboard implements OnInit {
    private http = inject(HttpClient);
    public leaderboardData$!: Observable<UserStats[]>;

    ngOnInit(): void {
        this.leaderboardData$ = this.fetchLeaderboard();
    }

    fetchLeaderboard(): Observable<UserStats[]> {
        return this.http.get<UserStats[]>('http://localhost:8080/api/leaderboard');
    }
}