import { Component, inject, Input, OnDestroy, OnInit, AfterViewChecked, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../services/web-socket';
import { CardModule } from 'primeng/card';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { InputGroupModule } from 'primeng/inputgroup';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';

export interface ChatMessage {
    sender: string;
    content: string;
    timestamp: number;
}

@Component({
    selector: 'app-chat-box',
    standalone: true,
    imports: [CommonModule, FormsModule, CardModule, ScrollPanelModule, InputGroupModule, InputTextModule, ButtonModule, RippleModule],
    templateUrl: './chat-box.html',
    styleUrls: ['./chat-box.scss']
})

export class ChatBox implements OnInit, OnDestroy, AfterViewChecked {
    @Input() gameId!: string;

    private webSocketService = inject(WebSocketService);
    private chatSubscription: Subscription | null = null;

    public messages: ChatMessage[] = [];
    public newMessage: string = '';

    currentUser: string = 'YourUsername';
    @ViewChild('scrollPanel') private scrollPanel!: ElementRef;

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

    ngAfterViewChecked() {
        this.scrollToBottom();
    }

    scrollToBottom(): void {
        try {
            const scrollContainer = this.scrollPanel.nativeElement.querySelector('.p-scrollpanel-content');
            scrollContainer.scrollTop = scrollContainer.scrollHeight;
        } catch (err) { }
    }
}