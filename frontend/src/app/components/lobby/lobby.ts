import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { GameService, GameState } from '../../services/game';
import { WebSocketService } from '../../services/web-socket';
import { Subscription } from 'rxjs';
import { PlayerProfile } from '../player-profile/player-profile';
import { AuthService } from '../../services/auth';
import { jwtDecode } from 'jwt-decode';
import { RouterModule } from '@angular/router';

@Component({
    selector: 'app-lobby',
    standalone: true,
    imports: [CommonModule, PlayerProfile, RouterModule],
    templateUrl: './lobby.html',
    styleUrls: ['./lobby.scss']
})

export class Lobby implements OnInit {
    public Object = Object;
    private gameService = inject(GameService);
    private webSocketService = inject(WebSocketService);
    public activeGames: GameState[] = [];
    private lobbySubscription: Subscription | null = null;
    private authService = inject(AuthService);
    public isAdmin = false;

    ngOnInit(): void {
        this.loadGames();
        // Connect to WebSocket and subscribe for real-time updates
        this.webSocketService.connect();
        this.lobbySubscription = this.webSocketService
            .subscribeToTopic<GameState[]>('/topic/lobby')
            .subscribe(games => {
                console.log('Received lobby update from WebSocket:', games);
                this.activeGames = games;
            });

        this.checkAdminStatus();
    }

    private checkAdminStatus(): void {
        const token = this.authService.getToken();
        if (token) {
            const decodedToken: { roles: string[] } = jwtDecode(token);
            this.isAdmin = decodedToken.roles.includes('ROLE_ADMIN');
        }
    }

    ngOnDestroy(): void {
        // Clean up the subscription when the component is destroyed
        if (this.lobbySubscription) {
            this.lobbySubscription.unsubscribe();
        }
        // Optionally disconnect if the user is leaving the app entirely
        // this.webSocketService.disconnect();
    }

    loadGames(): void {
        this.gameService.getActiveGames().subscribe(games => {
            this.activeGames = games;
        });
    }

    onJoinGame(gameId: string): void {
        this.gameService.joinGame(gameId);
    }
}
