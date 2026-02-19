package com.revhire.service;

import com.revhire.dto.favourites.FavouriteJobResponse;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface FavouriteService {
    void addFavourite(UserDetails currentUser, Long jobId);
    List<FavouriteJobResponse> getMyFavourites(UserDetails currentUser);
    void removeFavourite(UserDetails currentUser, Long jobId);
}