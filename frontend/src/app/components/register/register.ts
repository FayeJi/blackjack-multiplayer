import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './register.html',
    styleUrls: ['./register.scss']
})
export class Register {
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);

    registerForm: FormGroup;
    errorMessage: string | null = null;
    successMessage: string | null = null;

    constructor() {
        this.registerForm = this.fb.group({
            username: ['', [Validators.required, Validators.minLength(3)]],
            password: ['', [Validators.required, Validators.minLength(4)]]
            // You could add a 'confirmPassword' field here with a custom validator
        });
    }

    onSubmit(): void {
        if (this.registerForm.invalid) {
            return; // Stop if the form is invalid
        }

        this.errorMessage = null;
        this.successMessage = null;

        this.authService.register(this.registerForm.value).subscribe({
            next: (response) => {
                this.successMessage = "Registration successful! You will be redirected to the login page.";
                // Navigate to the login page after a short delay
                setTimeout(() => {
                    this.router.navigate(['/login']);
                }, 2000); // 2-second delay
            },
            error: (err) => {
                console.error('Registration failed', err);
                // The backend sends a specific error message which we can display
                this.errorMessage = err.error || 'An unknown registration error occurred.';
            }
        });
    }
}