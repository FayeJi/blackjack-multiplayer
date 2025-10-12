import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService, UserStats } from '../../services/user';

@Component({
    selector: 'app-player-profile',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './player-profile.html',
    styleUrls: ['./player-profile.scss']
})
export class PlayerProfile implements OnInit {
    private userService = inject(UserService);
    public stats: UserStats | null = null;

    ngOnInit(): void {
        this.userService.getMyStats().subscribe(data => {
            this.stats = data;
        });
    }

    get winRate(): number {
        if (!this.stats || this.stats.handsPlayed === 0) {
            return 0;
        }
        return (this.stats.handsWon / this.stats.handsPlayed) * 100;
    }
}