import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { WebSocketService } from './web-socket';

export interface Card {
    suit: string;
    rank: string;
}

export interface Hand {
    cards: Card[];
    value: number;
}

export interface GameState {
    gameId: string;
    roomName: string;
    playerHands: { [username: string]: Hand };
    dealerHand: Hand;
    statusMessage: string;
    phase: string;
    activePlayerUsername: string;
    playerOrder: string[];
}

@Injectable({
    providedIn: 'root'
})

export class GameService {
    private apiUrl = 'http://localhost:8080/api/games';
    private http = inject(HttpClient);
    private router = inject(Router);
    private webSocketService = inject(WebSocketService);

    getActiveGames(): Observable<GameState[]> {
        return this.http.get<GameState[]>(this.apiUrl);
    }

    joinGame(gameId: string): void {
        this.http.post<GameState>(`${this.apiUrl}/${gameId}/join`, {}).subscribe({
            next: (game) => {
                this.router.navigate(['/game', game.gameId]);
            },
            error: (err) => {
                console.error('Failed to join game', err);
            }
        });
    }

    getGameById(gameId: string): Observable<GameState> {
        return this.http.get<GameState>(`${this.apiUrl}/${gameId}`);
    }

    leaveGame(gameId: string): void {
        const destination = `/app/game/${gameId}/action`;
        this.webSocketService.sendMessage(destination, { action: 'leave' });
        this.router.navigate(['/lobby']);
    }
}