import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService, UserStats } from '../../services/user';
import { CardModule } from 'primeng/card';
import { AvatarModule } from 'primeng/avatar';
import { DividerModule } from 'primeng/divider';
import { KnobModule } from 'primeng/knob';

@Component({
    selector: 'app-player-profile',
    standalone: true,
    imports: [CommonModule, CardModule, AvatarModule, DividerModule, KnobModule],
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