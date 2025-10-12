import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule], // Import RouterModule for routerLink
    templateUrl: './login.html',
    styleUrls: ['./login.scss']
})
export class Login {
    // Dependency Injection using inject()
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    loginForm: FormGroup;
    errorMessage: string | null = null;

    constructor() {
        this.loginForm = this.fb.group({
            username: ['', [Validators.required]],
            password: ['', [Validators.required]]
        });
    }

    onSubmit(): void {
        if (this.loginForm.invalid) {
            return; // Don't submit if the form is invalid
        }

        this.errorMessage = null; // Clear previous errors
        this.authService.login(this.loginForm.value).subscribe({
            next: () => {
                // On successful login, navigate to the lobby
                this.router.navigate(['/lobby']);
            },
            error: (err) => {
                // Handle login errors (e.g., bad credentials)
                console.error('Login failed', err);
                this.errorMessage = 'Invalid username or password. Please try again.';
            }
        });
    }
}