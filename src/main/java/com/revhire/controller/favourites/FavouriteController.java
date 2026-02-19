package com.revhire.controller.favourites;

import com.revhire.dto.ApiResponse;
import com.revhire.dto.favourites.FavouriteJobResponse;
import com.revhire.service.FavouriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favourites")
@RequiredArgsConstructor
public class FavouriteController {

    private final FavouriteService favouriteService;

    @PostMapping("/{jobId}")
    public ApiResponse<Void> addFavourite(@AuthenticationPrincipal UserDetails currentUser,
                                          @PathVariable Long jobId) {
        favouriteService.addFavourite(currentUser, jobId);
        return ApiResponse.success("Job added to favourites", null);
    }

    @GetMapping("/my")
    public ApiResponse<List<FavouriteJobResponse>> getMyFavourites(@AuthenticationPrincipal UserDetails currentUser) {
        List<FavouriteJobResponse> favourites = favouriteService.getMyFavourites(currentUser);
        return ApiResponse.success("Favourites retrieved", favourites);
    }

    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> removeFavourite(@AuthenticationPrincipal UserDetails currentUser,
                                             @PathVariable Long jobId) {
        favouriteService.removeFavourite(currentUser, jobId);
        return ApiResponse.success("Job removed from favourites", null);
    }
}