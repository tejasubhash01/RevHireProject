package com.revhire.dto.favourites;

import com.revhire.dto.jobs.JobPostDto;
import lombok.Data;

@Data
public class FavouriteJobResponse {
    private Long id;
    private JobPostDto jobPost;
}