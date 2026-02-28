import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FavouritesService, FavouriteJobResponse } from '../../../../core/services/favourites.service';

@Component({
  selector: 'app-favourites',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './favourites.component.html',
  styleUrls: ['./favourites.component.css']
})
export class FavouritesComponent implements OnInit {
  favourites: FavouriteJobResponse[] = [];
  loading = true;
  error = '';

  constructor(private favouritesService: FavouritesService) {}

  ngOnInit() {
    this.loadFavourites();
  }

  loadFavourites() {
    this.favouritesService.getMyFavourites().subscribe({
      next: (data) => {
        this.favourites = data;
        this.loading = false;
      },
      error: (err) => {
        console.error(err);
        this.error = 'Failed to load favourites.';
        this.loading = false;
      }
    });
  }

  remove(jobId: number) {
    this.favouritesService.removeFavourite(jobId).subscribe({
      next: () => {
        this.loadFavourites(); // refresh list
      },
      error: (err) => {
        console.error(err);
        alert('Failed to remove.');
      }
    });
  }
}
