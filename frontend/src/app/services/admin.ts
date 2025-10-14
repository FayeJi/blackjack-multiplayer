import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { UserStats } from './user';

@Injectable({
    providedIn: 'root'
})

export class AdminService {
    private apiUrl = 'http://localhost:8080/api/admin';
    private http = inject(HttpClient);

    getAllUsers(): Observable<UserStats[]> {
        return this.http.get<UserStats[]>(`${this.apiUrl}/users`);
    }
}