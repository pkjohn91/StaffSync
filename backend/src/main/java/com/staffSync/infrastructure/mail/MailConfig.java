package com.staffSync.infrastructure.mail;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * 이메일 발송을 위한 JavaMailSender 설정
 * 프로바이더(Gmail, Naver, Kakao)에 따라 동적으로 SMTP 설정을 변경
 */
@Configuration
@RequiredArgsConstructor
public class MailConfig {

    private final MailProperties mailProperties;

    // Gmail 설정
    @Value("${mail.gmail.host}")
    private String gmailHost;
    @Value("${mail.gmail.port}")
    private int gmailPort;
    @Value("${mail.gmail.username}")
    private String gmailUsername;
    @Value("${mail.gmail.password}")
    private String gmailPassword;
    @Value("${mail.gmail.auth}")
    private boolean gmailAuth;
    @Value("${mail.gmail.starttls.enable}")
    private boolean gmailStarttls;

    // Naver 설정
    @Value("${mail.naver.host}")
    private String naverHost;
    @Value("${mail.naver.port}")
    private int naverPort;
    @Value("${mail.naver.username}")
    private String naverUsername;
    @Value("${mail.naver.password}")
    private String naverPassword;
    @Value("${mail.naver.auth}")
    private boolean naverAuth;
    @Value("${mail.naver.ssl.enable}")
    private boolean naverSsl;

    // Kakao(Daum) 설정
    @Value("${mail.kakao.host}")
    private String kakaoHost;
    @Value("${mail.kakao.port}")
    private int kakaoPort;
    @Value("${mail.kakao.username}")
    private String kakaoUsername;
    @Value("${mail.kakao.password}")
    private String kakaoPassword;
    @Value("${mail.kakao.auth}")
    private boolean kakaoAuth;
    @Value("${mail.kakao.ssl.enable}")
    private boolean kakaoSsl;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        String provider = mailProperties.getProvider().toLowerCase();

        switch (provider) {
            case "gmail":
                configureGmail(mailSender);
                System.out.println("📧 이메일 설정: Gmail SMTP 사용");
                break;
            case "naver":
                configureNaver(mailSender);
                System.out.println("📧 이메일 설정: Naver SMTP 사용");
                break;
            case "kakao":
                configureKakao(mailSender);
                System.out.println("📧 이메일 설정: Kakao(Daum) SMTP 사용");
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 이메일 제공자: " + provider);
        }

        return mailSender;
    }

    /**
     * Gmail SMTP 설정
     */
    private void configureGmail(JavaMailSenderImpl mailSender) {
        mailSender.setHost(gmailHost);
        mailSender.setPort(gmailPort);
        mailSender.setUsername(gmailUsername);
        mailSender.setPassword(gmailPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", gmailAuth);
        props.put("mail.smtp.starttls.enable", gmailStarttls);
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.debug", "false"); // true로 설정하면 상세 로그 출력
    }

    /**
     * Naver SMTP 설정
     */
    private void configureNaver(JavaMailSenderImpl mailSender) {
        mailSender.setHost(naverHost);
        mailSender.setPort(naverPort);
        mailSender.setUsername(naverUsername);
        mailSender.setPassword(naverPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", naverAuth);
        props.put("mail.smtp.ssl.enable", naverSsl);
        props.put("mail.smtp.ssl.trust", "smtp.naver.com");
        props.put("mail.debug", "false");
    }

    /**
     * Kakao(Daum) SMTP 설정
     */
    private void configureKakao(JavaMailSenderImpl mailSender) {
        mailSender.setHost(kakaoHost);
        mailSender.setPort(kakaoPort);
        mailSender.setUsername(kakaoUsername);
        mailSender.setPassword(kakaoPassword);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", kakaoAuth);
        props.put("mail.smtp.ssl.enable", kakaoSsl);
        props.put("mail.smtp.ssl.trust", "smtp.daum.net");
        props.put("mail.debug", "false");
    }
}