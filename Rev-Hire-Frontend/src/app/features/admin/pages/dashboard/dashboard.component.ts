import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AdminService, DbCheck } from '../../../../core/services/admin.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  stats: DbCheck = { userCount: 0 };
  pingMessage = '';
  loading = true;
  error = '';

  constructor(private adminService: AdminService) {}

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.adminService.getDbCheck().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load database stats.';
        this.loading = false;
      }
    });


    this.adminService.ping().subscribe({
      next: (msg) => this.pingMessage = msg,
      error: (err) => console.error(err)
    });
  }
}
