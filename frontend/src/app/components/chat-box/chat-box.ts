import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../services/web-socket';

export interface ChatMessage {
    sender: string;
    content: string;
    timestamp: number;
}

@Component({
    selector: 'app-chat-box',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './chat-box.html',
    styleUrls: ['./chat-box.scss']
})
export class ChatBox implements OnInit, OnDestroy {
    @Input() gameId!: string;

    private webSocketService = inject(WebSocketService);
    private chatSubscription: Subscription | null = null;

    public messages: ChatMessage[] = [];
    public newMessage: string = '';

    ngOnInit(): void {
        if (this.gameId) {
            const topic = `/topic/game/${this.gameId}/chat`;
            this.chatSubscription = this.webSocketService
                .subscribeToTopic<ChatMessage>(topic)
                .subscribe(message => {
                    this.messages.push(message);
                });
        }
    }

    ngOnDestroy(): void {
        if (this.chatSubscription) {
            this.chatSubscription.unsubscribe();
        }
    }

    sendMessage(): void {
        if (this.newMessage.trim() && this.gameId) {
            const destination = `/app/game/${this.gameId}/chat`;
            this.webSocketService.sendMessage(destination, { content: this.newMessage });
            this.newMessage = '';
        }
    }
}