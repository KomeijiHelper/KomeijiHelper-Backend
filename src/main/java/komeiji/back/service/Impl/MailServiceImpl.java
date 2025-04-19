package komeiji.back.service.Impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import komeiji.back.service.EmailCaptchaType;
import komeiji.back.service.EmailCodeStatus;
import komeiji.back.service.MailService;
import komeiji.back.utils.RedisUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

class RandomUtils {
    private static final Logger logger = LoggerFactory.getLogger(RandomUtils.class);
    private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String randomCode(String algorithm,int length) {
        SecureRandom randomBuilder;
        try {
            randomBuilder = SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e.getMessage());
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(randomBuilder.nextInt(chars.length())));
        }
        return sb.toString();
    }
}

@Service
public class MailServiceImpl implements MailService {

    private static final Logger logger = LoggerFactory.getLogger(MailServiceImpl.class);

    @Value("${spring.mail.username}")
    private String from;

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private RedisUtils redisUtils;


    @Override
    public void test(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }


    public EmailCodeStatus sendResetPasswordMail(String to, String subject, String name) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
            setMimeMailHeader(mimeMessageHelper,to,subject);

            Context context = new Context();
            context.setVariable("name", name);
            String code = RandomUtils.randomCode("SHA1PRNG",8);
            if(code == null) {
                return EmailCodeStatus.VERIFICATION_CODE_GENERATION_FAILED;
            }
            context.setVariable("password", code);

            String emailContent = templateEngine.process("mail-template-resetPassword", context);
            mimeMessageHelper.setText(emailContent, true);

            mailSender.send(mimeMessage);

        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
            return EmailCodeStatus.EMAIL_SEND_FAILED;
        }
        return EmailCodeStatus.SUCCESS;
    }

    @Override
    public EmailCodeStatus sendCaptcha(String to, String subject, String name, EmailCaptchaType type,
                                       long sendTimeLimit, long validTime, TimeUnit unit) {
        String sendKey = String.format("email:send_flag:%s:%s",type.getValue(),to);

        Boolean sendTimeout = redisUtils.tryLock(sendKey,"1",sendTimeLimit,unit);

        if(!Boolean.TRUE.equals(sendTimeout)) {
            return EmailCodeStatus.RATE_LIMIT_EXCEEDED;
        }

        String code = RandomUtils.randomCode("SHA1PRNG",6);
        if(code == null) {
            return EmailCodeStatus.VERIFICATION_CODE_GENERATION_FAILED;
        }
        code = code.toUpperCase();
        logger.info("{}的{}验证码是:{}",to,type.getDescription(),code);

        String codeKey = String.format("email:code:%s:%s",type.getValue(),to);
        redisUtils.set(codeKey,code,validTime,unit);

        try {
            sendCaptchaTemplateMail(to,subject,name,validTime,unit,code,type);
        } catch (MessagingException | UnsupportedEncodingException e) {
            logger.warn(e.getMessage());
            return EmailCodeStatus.EMAIL_SEND_FAILED;
        }

        return EmailCodeStatus.SUCCESS;
    }

    @Override
    public EmailCodeStatus checkCaptcha(String to, EmailCaptchaType type, @NotNull String inputCode) {
        String codKey = String.format("email:code:%s:%s",type.getValue(),to);
        String realCode = redisUtils.getString(codKey);
        logger.info("获取到{}的验证码:{}",to,realCode);

        if(realCode == null) {
            return EmailCodeStatus.CODE_EXPIRED;
        }

        if(!realCode.equals(inputCode)) {
            return EmailCodeStatus.CODE_ERROR;
        }

        redisUtils.delete(codKey);
        return EmailCodeStatus.CODE_VERIFIED;
    }

    private void setMimeMailHeader(MimeMessageHelper helper,String to,String subject) throws UnsupportedEncodingException, MessagingException {
        helper.setFrom(new InternetAddress(from,"Komeiji PlatForm","UTF-8"));
        helper.setTo(to);
        helper.setSubject(subject);
    }

    private void sendCaptchaTemplateMail(String to,String subject,String name,
                                         long validTime,TimeUnit unit,
                                         String code,EmailCaptchaType type) throws MessagingException, UnsupportedEncodingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);
        setMimeMailHeader(mimeMessageHelper,to,subject);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("type",type.getDescription());
        context.setVariable("code", code);
        context.setVariable("validTime", validTime);
        // TODO: Timeunit to String
        context.setVariable("unit", unit);
        String emailContent = templateEngine.process("mail-template-captcha", context);
        mimeMessageHelper.setText(emailContent, true);

        mailSender.send(mimeMessage);
    }



}
