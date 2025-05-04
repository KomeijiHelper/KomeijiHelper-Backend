package komeiji.back;

import jakarta.annotation.Resource;
import komeiji.back.service.EmailCaptchaType;
import komeiji.back.service.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.TimeUnit;

@SpringBootTest
public class MailTests {

    @Resource
    private MailService mailService;

    // 这里请用自己的邮箱测试，并自行修改测试样例
    private final String testEmail = "junqiaochen860@gmail.com";
    @Test
    public void test() {
        mailService.test(testEmail,"test","test");
    }

    @Test
    public void testResetPassword() {
        System.out.println(mailService.sendResetPasswordMail(testEmail,"重置密码","Tof","123456").getMessage());
    }

    @Test
    public void testCaptcha() {
        System.out.println(mailService.sendCaptcha(testEmail,"注册验证码","Tof",
                EmailCaptchaType.REGISTER,1,5, TimeUnit.MINUTES).getMessage());
    }

    @Test
    public void testCheckCaptcha() {
        System.out.println(mailService.checkCaptcha(testEmail,EmailCaptchaType.REGISTER,"RRASIS").getMessage());
    }
}
