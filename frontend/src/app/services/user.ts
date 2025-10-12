import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

// Create an interface for the stats DTO
export interface UserStats {
    username: string;
    balance: number;
    handsPlayed: number;
    handsWon: number;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8080/api/user';
    private http = inject(HttpClient);

    getMyStats(): Observable<UserStats> {
        return this.http.get<UserStats>(`${this.apiUrl}/me`);
    }
}