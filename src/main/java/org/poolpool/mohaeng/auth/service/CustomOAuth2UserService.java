package org.poolpool.mohaeng.auth.service;

import java.util.Optional;

import org.poolpool.mohaeng.user.entity.SocialUserEntity;
import org.poolpool.mohaeng.user.entity.UserEntity;
import org.poolpool.mohaeng.user.repository.SocialUserRepository;
import org.poolpool.mohaeng.user.repository.UserRepository;
import org.poolpool.mohaeng.user.type.SignupType;
import org.poolpool.mohaeng.user.type.UserRole;
import org.poolpool.mohaeng.user.type.UserStatus;
import org.poolpool.mohaeng.user.type.UserType;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;
    private final SocialUserRepository socialUserRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
    	
    	OidcUser oauth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        String providerId = oauth2User.getAttribute("sub"); 
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");

        //이메일 기준 기존 회원인지 조회
        Optional<UserEntity> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            UserEntity existingUser = optionalUser.get();

            //일반 회원가입 회원이면 소셜 연동 불가
            if (existingUser.getSignupType() == SignupType.BASIC) {
                throw new OAuth2AuthenticationException(
                    "해당 이메일은 일반 회원가입 계정입니다. 소셜 로그인으로 연동할 수 없습니다."
                );
            }

            //이미 소셜 계정 연결되어 있으면 그대로 사용
            return oauth2User;
        }

        //신규 가입 (USERS + SOCIAL_USER 생성)
        UserEntity user = userRepository.save(
                UserEntity.builder()
                        .email(email)
                        .name(name)
                        .signupType(SignupType.GOOGLE)
                        .userType(UserType.PERSONAL)
                        .userRole(UserRole.USER)
                        .userStatus(UserStatus.ACTIVE)
                        .build()
        );

        socialUserRepository.save(
                SocialUserEntity.builder()
                        .user(user)
                        .provider(provider)
                        .providerId(providerId)
                        .build()
        );

        return oauth2User;
    }
}
