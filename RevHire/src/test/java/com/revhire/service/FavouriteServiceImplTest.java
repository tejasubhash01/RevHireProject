package com.revhire.service;

import com.revhire.entity.EmployerProfile;
import com.revhire.entity.FavouriteJob;
import com.revhire.entity.JobPost;
import com.revhire.entity.User;
import com.revhire.entity.enums.Role;
import com.revhire.exception.BadRequestException;
import com.revhire.exception.NotFoundException;
import com.revhire.repository.FavouriteJobRepository;
import com.revhire.repository.JobPostRepository;
import com.revhire.repository.UserRepository;
import com.revhire.service.impl.FavouriteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FavouriteServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private JobPostRepository jobPostRepository;
    @Mock
    private FavouriteJobRepository favouriteRepository;

    @InjectMocks
    private FavouriteServiceImpl favouriteService;

    private UserDetails mockUserDetails;
    private User mockUser;
    private EmployerProfile mockEmployer;
    private JobPost mockJobPost;
    private FavouriteJob mockFavourite;

    @BeforeEach
    void setUp() {
        mockUserDetails = org.springframework.security.core.userdetails.User
                .withUsername("user@test.com")
                .password("pass")
                .roles("JOB_SEEKER")
                .build();

        mockUser = User.builder()
                .email("user@test.com")
                .name("Test User")
                .role(Role.JOB_SEEKER)
                .build();
        mockUser.setId(1L);

        // Create an employer profile with a company name
        mockEmployer = EmployerProfile.builder()
                .companyName("TestCorp")
                .build();
        mockEmployer.setId(10L);

        mockJobPost = JobPost.builder()
                .title("Java Developer")
                .employer(mockEmployer)  // Set the employer
                .build();
        mockJobPost.setId(100L);

        mockFavourite = FavouriteJob.builder()
                .user(mockUser)
                .jobPost(mockJobPost)
                .build();
        mockFavourite.setId(1000L);
    }

    @Test
    void addFavourite_Success() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(jobPostRepository.findById(100L)).thenReturn(Optional.of(mockJobPost));
        when(favouriteRepository.existsByUserIdAndJobPostId(mockUser.getId(), 100L)).thenReturn(false);
        when(favouriteRepository.save(any(FavouriteJob.class))).thenReturn(mockFavourite);

        favouriteService.addFavourite(mockUserDetails, 100L);

        verify(favouriteRepository, times(1)).save(any(FavouriteJob.class));
    }

    @Test
    void addFavourite_AlreadyExists_ThrowsBadRequest() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(jobPostRepository.findById(100L)).thenReturn(Optional.of(mockJobPost));
        when(favouriteRepository.existsByUserIdAndJobPostId(mockUser.getId(), 100L)).thenReturn(true);

        assertThrows(BadRequestException.class, () -> favouriteService.addFavourite(mockUserDetails, 100L));
    }

    @Test
    void getMyFavourites_ReturnsList() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(favouriteRepository.findByUserId(mockUser.getId())).thenReturn(List.of(mockFavourite));

        var result = favouriteService.getMyFavourites(mockUserDetails);

        assertEquals(1, result.size());
        assertEquals(mockFavourite.getId(), result.get(0).getId());
        // Optionally verify that the DTO contains the expected job title and company name
        assertEquals("Java Developer", result.get(0).getJobPost().getTitle());
        assertEquals("TestCorp", result.get(0).getJobPost().getCompanyName());
    }

    @Test
    void removeFavourite_Success() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(favouriteRepository.findByUserIdAndJobPostId(mockUser.getId(), 100L)).thenReturn(Optional.of(mockFavourite));
        doNothing().when(favouriteRepository).delete(mockFavourite);

        favouriteService.removeFavourite(mockUserDetails, 100L);

        verify(favouriteRepository, times(1)).delete(mockFavourite);
    }

    @Test
    void removeFavourite_NotFound_ThrowsNotFoundException() {
        when(userRepository.findByEmail(mockUserDetails.getUsername())).thenReturn(Optional.of(mockUser));
        when(favouriteRepository.findByUserIdAndJobPostId(mockUser.getId(), 100L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> favouriteService.removeFavourite(mockUserDetails, 100L));
    }
}