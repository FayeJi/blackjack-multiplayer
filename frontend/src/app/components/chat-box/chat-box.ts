import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../services/web-socket';

// Define the shape of a chat message object
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
    @Input() gameId!: string; // The parent component (GameTable) will provide the gameId

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
                    // Optional: Add logic to auto-scroll to the bottom
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
            this.newMessage = ''; // Clear the input box
        }
    }
}