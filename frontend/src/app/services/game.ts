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
    value: number; // We'll calculate this on the fly for display
}

// Define an interface for our GameState object on the frontend
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

export interface GameState {
    // ... existing properties
    playerReadyStatus: { [username: string]: boolean; }; // <-- Add the new property
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
                // On successfully joining, navigate to the game table page
                this.router.navigate(['/game', game.gameId]);
            },
            error: (err) => {
                console.error('Failed to join game', err);
                // You can add user-facing error handling here
            }
        });
    }

    getGameById(gameId: string): Observable<GameState> { // We can reuse the GameState interface
        return this.http.get<GameState>(`${this.apiUrl}/${gameId}`);
    }

    leaveGame(gameId: string): void {
        // 1. Send the "leave" message to the backend via WebSocket
        const destination = `/app/game/${gameId}/action`;
        this.webSocketService.sendMessage(destination, { action: 'leave' });

        // 2. Immediately navigate the user back to the lobby
        this.router.navigate(['/lobby']);
    }
}