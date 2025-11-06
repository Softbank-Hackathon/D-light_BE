package com.hackathon.melon.domain.user.entity;

import com.hackathon.melon.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long githubId;

    private String login;
    @Column(columnDefinition = "text")
    private String profileUrl;
    private String email;

    public static User of(Long githubId, String login, String profileUrl, String email){
        User u = new User();
        u.githubId = githubId; u.login = login; u.profileUrl = profileUrl; u.email = email;
        return u;
    }
}
