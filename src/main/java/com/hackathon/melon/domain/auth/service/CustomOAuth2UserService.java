package com.hackathon.melon.domain.auth.service;

import com.hackathon.melon.domain.user.entity.User;
import com.hackathon.melon.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest req) throws OAuth2AuthenticationException {
        OAuth2User delegate = super.loadUser(req);
        Map<String,Object> a = delegate.getAttributes();

        Long githubId  = ((Number)a.get("id")).longValue();
        String login   = (String) a.get("login");
        String email   = (String) a.get("email");     // null 가능
        String htmlUrl = (String) a.get("html_url");

        User user = userRepository.findByGithubId(githubId)
                .map(u -> { u.setLogin(login); u.setProfileUrl(htmlUrl); u.setEmail(email); return u; })
                .orElseGet(() -> User.of(githubId, login, htmlUrl, email));
        userRepository.save(user);

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                a,
                "id"
        );
    }
}
