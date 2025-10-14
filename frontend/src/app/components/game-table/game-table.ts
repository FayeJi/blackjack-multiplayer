import { Component, inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../services/web-socket';
import { GameState, Hand } from '../../services/game';
import { KeyValue } from '@angular/common';
import { AuthService } from '../../services/auth';
import { FormsModule } from '@angular/forms';
import { ChatBox } from '../chat-box/chat-box';
import { GameService } from '../../services/game';

@Component({
    selector: 'app-game-table',
    standalone: true,
    imports: [CommonModule, FormsModule, ChatBox],
    templateUrl: './game-table.html',
    styleUrls: ['./game-table.scss']
})

export class GameTable implements OnInit, OnDestroy {
    public betAmount: number = 10;
    public Object = Object;
    private route = inject(ActivatedRoute);
    private webSocketService = inject(WebSocketService);
    private authService = inject(AuthService);
    private gameService = inject(GameService);

    public gameState: GameState | null = null;
    private myUsername: string | null = null;
    private gameId: string | null = null;
    private gameSubscription: Subscription | null = null;

    originalOrder = (a: KeyValue<string, any>, b: KeyValue<string, any>): number => {
        return 0;
    }

    ngOnInit(): void {
        this.authService.currentUserUsername$.subscribe(username => {
            this.myUsername = username;
        });

        this.gameId = this.route.snapshot.paramMap.get('id');

        if (this.gameId) {
            this.gameService.getGameById(this.gameId).subscribe({
                next: (initialState) => {
                    this.gameState = initialState;
                    this.connectToWebSocket();
                },
                error: (err) => {
                    console.error('Failed to fetch initial game state', err);
                    
                }
            });
        }
    }

    connectToWebSocket(): void {
        if (!this.gameId) return;

        this.webSocketService.connect();
        const topic = `/topic/game/${this.gameId}`;

        if (this.gameSubscription) {
            this.gameSubscription.unsubscribe();
        }

        this.gameSubscription = this.webSocketService
            .subscribeToTopic<GameState>(topic)
            .subscribe(updatedState => {
                console.log('Received game state update from WebSocket:', updatedState);
                this.gameState = updatedState;
            });
    }

    ngOnDestroy(): void {
        if (this.gameSubscription) {
            this.gameSubscription.unsubscribe();
        }
    }

    sendPlayerAction(action: string): void {
        if (this.gameId) {
            const destination = `/app/game/${this.gameId}/action`;
            this.webSocketService.sendMessage(destination, { action });
        }
    }

    getHandValue(hand: Hand): number {
        let value = 0;
        let aceCount = 0;
        for (const card of hand.cards) {
            switch (card.rank) {
                case 'ACE': aceCount++; value += 11; break;
                case 'KING':
                case 'QUEEN':
                case 'JACK':
                case 'TEN': value += 10; break;
                case 'NINE': value += 9; break;
                case 'EIGHT': value += 8; break;
                case 'SEVEN': value += 7; break;
                case 'SIX': value += 6; break;
                case 'FIVE': value += 5; break;
                case 'FOUR': value += 4; break;
                case 'THREE': value += 3; break;
                case 'TWO': value += 2; break;
            }
        }

        while (value > 21 && aceCount > 0) {
            value -= 10;
            aceCount--;
        }
        return value;
    }

    isMyTurn(): boolean {
        if (!this.myUsername || !this.gameState || !this.gameState.activePlayerUsername) {
            return false;
        }
        return this.myUsername === this.gameState.activePlayerUsername;
    }

    onPlaceBet(): void {
        if (this.gameId && this.betAmount > 0) {
            const destination = `/app/game/${this.gameId}/bet`;
            this.webSocketService.sendMessage(destination, { amount: this.betAmount });
        }
    }

    onLeaveGame(): void {
        if (this.gameId) {
            this.gameService.leaveGame(this.gameId);
        }
    }

    isHost(): boolean {
        if (!this.myUsername || !this.gameState || !this.gameState.playerOrder || this.gameState.playerOrder.length === 0) {
            return false;
        }

        return this.myUsername === this.gameState.playerOrder[0];
    }
}