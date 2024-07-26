package com.LibraryManagementSystem.LMS.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "UserProfileImage")
public class ProfileImage {
    @Id
    @GeneratedValue
    private Long id;
    private String path;
    private String name;
    private LocalDateTime uploadDate;

    @OneToOne(mappedBy = "profileImage")
    private User user;

}
