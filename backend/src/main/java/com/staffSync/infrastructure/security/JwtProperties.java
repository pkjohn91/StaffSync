package com.staffSync.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

/**
 * JWT 관련 설정 값을 application.properties에서 주입 받는 클래스
 */

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret; // jwt 서명 키
    private Long accessTokenExpiration; // Access Token 만료 시간 (ms)
    private Long refreshTokenExpiration; // Refresh Token 만료 시간 (ms)
}
