package komeiji.back.service;

import java.util.concurrent.TimeUnit;

public interface MailService {
    void test(String to,String subject,String name);
    EmailCodeStatus sendResetPasswordMail(String to, String subject, String name);
    EmailCodeStatus sendCaptcha(String to, String subject, String name, EmailCaptchaType type,
                        long sendTimeLimit,long validTime, TimeUnit unit);

    EmailCodeStatus checkCaptcha(String to,EmailCaptchaType type,String inputCode);
}
