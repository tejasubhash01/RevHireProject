import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-enums',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './enums.component.html',
  styleUrls: ['./enums.component.css']
})
export class EnumsComponent implements OnInit {
  enums: any = null;
  loading = true;
  error = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.adminService.getEnums().subscribe({
      next: (data) => {
        this.enums = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load enums.';
        this.loading = false;
      }
    });
  }


  isArray(value: any): boolean {
    return Array.isArray(value);
  }

  objectKeys(obj: any): string[] {
    return obj ? Object.keys(obj) : [];
  }
}
