import { Injectable } from '@angular/core';
import * as Stomp from 'stompjs';
import SockJS from 'sockjs-client';
import { BehaviorSubject, Observable } from 'rxjs';
import { GameState } from './game';
import { AuthService } from './auth';

@Injectable({
    providedIn: 'root'
})
export class WebSocketService {
    private stompClient: Stomp.Client | null = null;
    private serverUrl = 'http://localhost:8080/ws';

    // BehaviorSubject to track connection status
    public isConnected: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);

    constructor(private authService: AuthService) { }

    // Establishes the WebSocket connection
    connect(): void {
        if (this.stompClient && this.stompClient.connected) {
            console.log('Already connected to WebSocket.');
            return;
        }

        const socket = new SockJS(this.serverUrl);
        this.stompClient = Stomp.over(socket);

        // Disable console logging from the stomp client
        this.stompClient.debug = () => { };

        // Create the connection headers object and add the JWT
        const token = this.authService.getToken();
        const headers = {
            Authorization: `Bearer ${token}`
        };

        this.stompClient.connect(headers, (frame) => {
            console.log('Connected to WebSocket:', frame);
            this.isConnected.next(true);
        }, (error) => {
            console.error('Connection error:', error);
            this.isConnected.next(false);
            // Optional: implement reconnection logic here
        });
    }

    // Subscribes to a specific topic and returns an Observable stream of messages
    subscribeToTopic<T>(topic: string): Observable<T> {
        return new Observable<T>(observer => {
            if (!this.stompClient || !this.stompClient.connected) {
                // Wait for connection before subscribing
                this.isConnected.subscribe(connected => {
                    if (connected) {
                        this.stompClient!.subscribe(topic, message => {
                            observer.next(JSON.parse(message.body) as T);
                        });
                    }
                });
            } else {
                // If already connected, subscribe immediately
                this.stompClient.subscribe(topic, message => {
                    observer.next(JSON.parse(message.body) as T);
                });
            }
        });
    }

    // Sends a message to a destination
    sendMessage(destination: string, body: any): void {
        if (!this.stompClient || !this.stompClient.connected) {
            console.error('Cannot send message. Not connected.');
            return;
        }
        this.stompClient.send(destination, {}, JSON.stringify(body));
    }

    // Disconnects from the WebSocket
    disconnect(): void {
        if (this.stompClient) {
            this.stompClient.disconnect(() => {
                console.log('Disconnected from WebSocket.');
                this.isConnected.next(false);
            });
        }
    }
}