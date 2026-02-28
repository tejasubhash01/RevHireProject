package com.revhire.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "employer_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerProfile extends BaseEntity {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    private String companyName;
    private String industry;
    private String companySize;
    @Column(length = 1000)
    private String companyDescription;
    private String website;
    private String location;

    @OneToMany(mappedBy = "employer", cascade = CascadeType.ALL)
    private List<JobPost> jobPosts;
}