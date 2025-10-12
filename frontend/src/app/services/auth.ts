import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode'

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = 'http://localhost:8080/api/auth'; // Your Spring Boot backend URL
    private http = inject(HttpClient);

    constructor() {
        this.loadUserFromToken();
    }

    private currentUserUsername = new BehaviorSubject<string | null>(null);
    public currentUserUsername$ = this.currentUserUsername.asObservable();

    private loadUserFromToken(): void {
        const token = this.getToken();
        if (token) {
            const decodedToken: { sub: string } = jwtDecode(token); // 'sub' is the standard claim for username
            this.currentUserUsername.next(decodedToken.sub);
        }
    }

    login(credentials: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/login`, credentials).pipe(
            tap((response: any) => {
                localStorage.setItem('access_token', response.accessToken);
                this.loadUserFromToken();
            })
        );
    }

    register(userInfo: any): Observable<any> {
        return this.http.post(`${this.apiUrl}/register`, userInfo);
    }

    public getToken(): string | null {
        return localStorage.getItem('access_token');
    }

    public isLoggedIn(): boolean {
        return this.getToken() !== null;
    }

    public logout(): void {
        localStorage.removeItem('access_token');
        this.currentUserUsername.next(null); // Notify all subscribers that the user logged out
        // Later, you'll navigate the user to the login page here
    }

    public getUsername(): string | null {
        return this.currentUserUsername.getValue();
    }

}