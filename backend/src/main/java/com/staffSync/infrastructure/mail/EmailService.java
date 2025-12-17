package com.staffSync.infrastructure.mail;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    // 임시 저장소(실무에서는 redis 사용 권장)
    private final Map<String, String> verificationStorage = new HashMap<>();

    /**
     * 인증 코드 이메일 발송 (텍스트)
     * 
     * @param toEmail 수신자 이메일
     * @param code    인증 코드
     */
    public void sendVerificationCode(String toEmail, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(toEmail);
        message.setSubject("[StaffSync] 인증 코드");
        message.setText(
                "안녕하세요, StaffSync 입니다.\n\n" +
                        "회원가입을 위한 인증 코드는 다음과 같습니다.\n\n" +
                        "인증 코드 : " + code + "\n\n" +
                        "이 코드는 10분동안 유효합니다.\n\n" +
                        "감사합니다.");

        mailSender.send(message);
        System.out.println("이메일 발송 완료: " + toEmail);
    }

    /**
     * 인증 코드 이메일 발송 (HTML) - ✅ 추가!
     * 
     * @param toEmail 수신자 이메일
     * @param code    인증 코드
     * @throws MessagingException 이메일 발송 실패 시
     */
    public void sendVerificationCodeHtml(String toEmail, String code) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("[StaffSync] 이메일 인증 코드");

        // HTML 이메일 템플릿
        String htmlContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f5f7fa;
                            padding: 20px;
                            margin: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 0 auto;
                            background-color: #ffffff;
                            border-radius: 12px;
                            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 40px 30px;
                            text-align: center;
                        }
                        .header h1 {
                            color: #ffffff;
                            margin: 0;
                            font-size: 28px;
                            font-weight: 600;
                        }
                        .header p {
                            color: #e0e7ff;
                            margin: 10px 0 0 0;
                            font-size: 14px;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .greeting {
                            font-size: 16px;
                            color: #333333;
                            margin-bottom: 20px;
                            line-height: 1.6;
                        }
                        .code-box {
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            padding: 30px;
                            border-radius: 10px;
                            text-align: center;
                            margin: 30px 0;
                            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
                        }
                        .code-label {
                            color: #ffffff;
                            font-size: 14px;
                            margin-bottom: 15px;
                            opacity: 0.9;
                        }
                        .code {
                            font-size: 42px;
                            font-weight: bold;
                            color: #ffffff;
                            letter-spacing: 8px;
                            font-family: 'Courier New', monospace;
                            text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
                        }
                        .info-box {
                            background-color: #fef3c7;
                            border-left: 4px solid #f59e0b;
                            padding: 15px 20px;
                            border-radius: 6px;
                            margin: 20px 0;
                        }
                        .info-box p {
                            margin: 0;
                            color: #92400e;
                            font-size: 14px;
                            line-height: 1.6;
                        }
                        .warning {
                            background-color: #fee2e2;
                            border-left: 4px solid #ef4444;
                            padding: 15px 20px;
                            border-radius: 6px;
                            margin: 20px 0;
                        }
                        .warning p {
                            margin: 0;
                            color: #991b1b;
                            font-size: 14px;
                        }
                        .footer {
                            text-align: center;
                            padding: 30px;
                            background-color: #f9fafb;
                            border-top: 1px solid #e5e7eb;
                        }
                        .footer p {
                            color: #6b7280;
                            font-size: 12px;
                            margin: 5px 0;
                        }
                        .footer a {
                            color: #667eea;
                            text-decoration: none;
                        }
                        .button {
                            display: inline-block;
                            padding: 12px 30px;
                            background: linear-gradient(135deg, #667eea 0%%, #764ba2 100%%);
                            color: #ffffff;
                            text-decoration: none;
                            border-radius: 6px;
                            font-weight: 600;
                            margin-top: 20px;
                            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <!-- 헤더 -->
                        <div class="header">
                            <h1>🏢 StaffSync</h1>
                            <p>HR Management System</p>
                        </div>

                        <!-- 본문 -->
                        <div class="content">
                            <div class="greeting">
                                <strong>안녕하세요!</strong><br>
                                StaffSync에 가입하신 것을 환영합니다.
                            </div>

                            <p style="color: #555; line-height: 1.6;">
                                회원가입을 완료하기 위해 아래의 인증 코드를 입력해주세요.
                            </p>

                            <!-- 인증 코드 박스 -->
                            <div class="code-box">
                                <div class="code-label">인증 코드</div>
                                <div class="code">%s</div>
                            </div>

                            <!-- 안내 사항 -->
                            <div class="info-box">
                                <p>
                                    ⏱️ 이 인증 코드는 <strong>10분간 유효</strong>합니다.<br>
                                    ⚠️ 시간 내에 인증을 완료해주세요.
                                </p>
                            </div>

                            <div class="warning">
                                <p>
                                    🔒 본인이 요청하지 않은 경우, 이 이메일을 무시하셔도 됩니다.<br>
                                    타인에게 인증 코드를 공유하지 마세요.
                                </p>
                            </div>
                        </div>

                        <!-- 푸터 -->
                        <div class="footer">
                            <p><strong>StaffSync Team</strong></p>
                            <p>© 2025 StaffSync. All rights reserved.</p>
                            <p>
                                궁금한 점이 있으시면 <a href="mailto:support@staffsync.com">support@staffsync.com</a>으로 문의해주세요.
                            </p>
                        </div>
                    </div>
                </body>
                </html>
                """, code);

        helper.setText(htmlContent, true); // true = HTML 형식

        mailSender.send(message);
        System.out.println("✅ HTML 이메일 발송 완료: " + toEmail);
    }

    // 코드 검증
    public boolean verifyCode(String email, String code) {
        String savedCode = verificationStorage.get(email);
        return savedCode != null && savedCode.equals(code);
    }

    private String generateCode() {
        return String.valueOf(100000 + new Random().nextInt(900000)); // 6자리 난수 생성
    }
}
