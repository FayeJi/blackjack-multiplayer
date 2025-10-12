import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Observable } from 'rxjs';
import { AdminService } from '../../../services/admin';
import { UserStats } from '../../../services/user';

@Component({
    selector: 'app-user-list',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './user-list.html',
    styleUrls: ['./user-list.scss']
})
export class UserList implements OnInit {
    private adminService = inject(AdminService);
    public users$!: Observable<UserStats[]>;

    ngOnInit(): void {
        this.users$ = this.adminService.getAllUsers();
    }
}