package komeiji.back.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import komeiji.back.dto.CaptchaDTO;
import komeiji.back.entity.User;
import komeiji.back.repository.UserDao;
import komeiji.back.service.EmailCaptchaType;
import komeiji.back.service.EmailCodeStatus;
import komeiji.back.service.MailService;
import komeiji.back.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/mail")
public class MailController {

    @Resource
    private MailService mailService;

    @Resource
    private UserDao userDao;

    @GetMapping("/sendCaptcha/changePwd")
    public Result<String> sendChangePwdCaptcha(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        String toMail = user.getEmail();
        EmailCodeStatus status = mailService.sendCaptcha(toMail,"修改密码验证",user.getUserName(), EmailCaptchaType.CHANGE_PASSWORD,
                1,5, TimeUnit.MINUTES);

        if(status == EmailCodeStatus.SUCCESS) {
            return Result.success(status.getMessage());
        }
        else {
            return Result.error(String.valueOf(status.getCode()), status.getMessage());
        }
    }

    @GetMapping("/sendCaptcha/register")
    public Result<String> sendRegisterCaptcha(@RequestParam String name,@RequestParam String email) {
        EmailCodeStatus status = mailService.sendCaptcha(email, "注册账号", name, EmailCaptchaType.REGISTER,
                1, 5, TimeUnit.MINUTES);
        if(status == EmailCodeStatus.SUCCESS) {
            return Result.success(status.getMessage());
        }
        else {
            return Result.error(String.valueOf(status.getCode()), status.getMessage());
        }
    }

    @GetMapping("/sendCaptcha/resetPwd")
    public Result<String> sendResetPwdCaptcha(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        String toMail = user.getEmail();
        EmailCodeStatus status = mailService.sendCaptcha(toMail, "重置密码", user.getUserName(), EmailCaptchaType.RESET_PASSWORD,
                1, 5, TimeUnit.MINUTES);

        if(status == EmailCodeStatus.SUCCESS) {
            return Result.success(status.getMessage());
        }
        else {
            return Result.error(String.valueOf(status.getCode()), status.getMessage());
        }
    }

    @PostMapping("/checkCaptcha")
    public Result<String> checkCaptcha(HttpSession session, @RequestBody CaptchaDTO captchaDTO) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        String toMail = user.getEmail();
        EmailCodeStatus status = mailService.checkCaptcha(toMail,
                EmailCaptchaType.fromValue(captchaDTO.getType()),captchaDTO.getCaptcha());
        if(status == EmailCodeStatus.CODE_VERIFIED) {
            return Result.success(status.getMessage());
        }
        else {
            return Result.error(String.valueOf(status.getCode()), status.getMessage());
        }
    }

    @PostMapping("/checkCaptcha/register")
    public Result<String> checkCaptcha(@RequestBody Map<String,String>body) {
        EmailCodeStatus status = mailService.checkCaptcha(body.get("email"),
                EmailCaptchaType.REGISTER,body.get("captcha"));
        if(status == EmailCodeStatus.CODE_VERIFIED) {
            return Result.success(status.getMessage());
        }
        return Result.error(String.valueOf(status.getCode()), status.getMessage());
    }

}
