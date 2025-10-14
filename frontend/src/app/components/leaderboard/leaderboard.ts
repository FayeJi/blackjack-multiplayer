import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RouterModule } from '@angular/router';
import { UserStats } from '../../services/user';

@Component({
    selector: 'app-leaderboard',
    standalone: true,
    imports: [CommonModule, RouterModule],
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