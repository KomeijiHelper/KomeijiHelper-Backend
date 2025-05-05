package komeiji.back.controller;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.UserDao;
import komeiji.back.service.EmailCodeStatus;
import komeiji.back.service.MailService;
import komeiji.back.service.UserService;
import komeiji.back.entity.User;
import komeiji.back.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import jakarta.annotation.Resource;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.UserDao;
import komeiji.back.service.EmailCodeStatus;
import komeiji.back.service.MailService;
import komeiji.back.service.UserService;
import komeiji.back.utils.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "用户基本功能", description = "用户基本功能接口")
@RestController
@RequestMapping(path ="/user")
public class UserController {
    @Resource
    private UserService userService;

    @Resource
    public RedisUtils redisUtils;

    @Resource
    UserDao userdao;

    @Resource
    MailService mailService;

    public static HashMap<String,HttpSession> sessions = new HashMap<>();

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "接受post请求体body中包含用户名和密码，返回登录成功的用户名")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "402", description = "账号或密码错误", content = @Content(schema = @Schema(implementation = Result.class)))

            }
    )
    public Result<String> loginController(@RequestBody User loginUser, HttpSession session, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        System.out.println("用户名:"+loginUser.getUserName()+loginUser.getPassword());

        User loginResult = userService.loginService(loginUser.getUserName(), loginUser.getPassword());

        if(loginResult!= null){
            if(redisUtils.isMember(RedisTable.loginUser, loginUser.getUserName())){
                return Result.error(409,"该用户已登录",response);
            }
            session.setAttribute("LoginUser", loginUser.getUserName());
            session.setAttribute("Id", loginResult.getId());

            //NOTICE 确保只有一个账户登录
            redisUtils.addSet(RedisTable.loginUser, loginUser.getUserName());


            return Result.success(loginUser.getUserName(), "登录成功");
        }
        else{
            return Result.error(402,"账号或密码错误", response);
        }
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "接受post请求体body中包含用户名、密码，返回注册成功的用户名")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "注册成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "456", description = "注册失败", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<String> registerController(@RequestBody User newUser, HttpSession session, HttpServletResponse response) throws IOException, NoSuchAlgorithmException {
        if(!userService.userNameIsLegal(newUser.getUserName())){
            System.out.println("注册用户名:"+newUser.getUserName());
            return Result.error(457,"用户名不合法",response);
        }
        if(!newUser.getQualification().isEmpty()){
            if(userdao.findByQualification(newUser.getQualification()) != null){
                return Result.error(460,"资质证书重复",response);
            }
        }
        Boolean registerResult = userService.registerService(newUser);
        if (registerResult) {
            session.setAttribute("LoginUser", newUser.getUserName());
            session.setAttribute("Id", newUser.getId());

            return Result.success(newUser.getUserName(), "注册成功");
        } else {
            return Result.error(456, "用户名重复", response);
        }
    }

    @GetMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，清除session")
    public void loginOut(HttpSession session) {
        redisUtils.removeSet(RedisTable.loginUser, session.getAttribute("LoginUser"));
        session.removeAttribute("LoginUser");
        session.removeAttribute("Id");
        session.invalidate();

    }

    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试接口")
    public String test(HttpServletRequest request) throws IOException, IllegalAccessException {
        String a = "abdfda";
        String b = "jkfadjlk";

        Object obj = Map.of("a",a,"b",b);
        redisUtils.addHash("cjw","jjj",obj);

       Object result =redisUtils.getHash("cjw","jjj");

        System.out.println(result);
        Map<String,Object> map = (Map<String, Object>) result;

        return result.toString();

    }


    @GetMapping("/getUserName")
    @Operation(summary = "获取当前登录用户的用户名", description = "获取当前登录用户的用户名")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "查找成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "未找到符合条件的用户", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<String> getUserName(HttpSession session, HttpServletResponse response) throws IOException {
        User user = getUserBySession(session);
        return user == null
                ? Result.error(401, "User Not Found", response)
                : Result.success(user.getUserName());
    }

    @PostMapping("/getUsersByClass")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "查找成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "401", description = "不存在该类用户", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    @Operation(summary = "根据用户类别获取用户列表", description = "获取成功返回用户列表，失败返回错误信息401")
    public Result<String> getUsersByClass(@RequestBody UserClassRequest userClassRequest, HttpServletResponse response, HttpSession session) throws IOException {
        UserClass requestUserClass = getUserBySession(session).getUserClass();
        Gson gson;
        ExclusionStrategy exclusionStrategy;
        if (requestUserClass != UserClass.Manager){
            exclusionStrategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    String fieldName = f.getName();
                    return fieldName.equals("password")
                            || fieldName.equals("id")
                            || fieldName.equals("userClass")
                            || fieldName.equals("email");
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false; // 不排除类
                }
            };
        } else {
            exclusionStrategy = new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes f) {
                    String fieldName = f.getName();
                    return fieldName.equals("password");

                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false; // 不排除类
                }
            };
        }

        gson = new GsonBuilder().setExclusionStrategies(exclusionStrategy).create();
        List<User> users;
        if (userClassRequest.userClassCode == -1){
            users = userService.getAllUsers();
        } else {
            UserClass userClass = UserClass.fromCode(userClassRequest.userClassCode);
            users = userService.getUsersByUserClass(userClass);
        }

        String result = gson.toJson(users);
        return users.isEmpty()
                ? Result.error(401, "User Not Found", response)
                : Result.success(result);
    }


    @PostMapping("/changeUser")
    @Operation(summary = "根据传入的User数据修改用户信息", description = "根据传入的User数据修改用户信息,manager权限可以任意修改，其他用户只能修改自己的信息")
    public Result<String> changeUser(@RequestBody User user,HttpSession session) throws NoSuchAlgorithmException {
        System.out.println(user.toString());
        User loginUser = userService.getUserById((long) session.getAttribute("Id"));
        if (loginUser.getUserClass() != UserClass.Manager) {
            if (user.getId() != (long) session.getAttribute("Id")) {
                return Result.error("-1", "权限不足，不能修改其他用户信息");
            } else {
                int result = userService.updateUser(user);
                if (result == 0) {
                    return Result.error("-2", "修改失败");
                } else {
                    return Result.success("修改成功");
                }
            }
        } else {
            int result = userService.updateUser(user);
            if (result == 0) {
                return Result.error("-2", "修改失败");
            } else {
                return Result.success("修改成功");
            }
        }
    }

    @PostMapping("/resetPassword")
    @Operation(summary = "根据传入的User数据修改用户信息", description = "根据传入的User数据修改用户信息,manager权限可以任意修改，其他用户只能修改自己的信息")
    public Result<String> resetPassword(@RequestBody int userId, HttpSession session) throws NoSuchAlgorithmException {
        User loginUser = userService.getUserById((long) session.getAttribute("Id"));
        User wantedUser = userService.getUserById(userId);
        if (loginUser.getId() == userId || loginUser.getUserClass() == UserClass.Manager) {
            String password = RandomUtils.randomCode("SHA1PRNG",8);
            int result = userService.updatePassword(wantedUser, password);
            EmailCodeStatus status = mailService.sendResetPasswordMail(wantedUser.getEmail(),"重置密码", wantedUser.getUserName(),password);
            if (result > 0 && status == EmailCodeStatus.SUCCESS) {
                return Result.success("修改成功");
            } else {
                return Result.error("-2", String.format("修改失败：%s",status.getMessage()));
            }
        } else return Result.error("-1", "不能修改其他用户的密码");
    }

    @PostMapping("/changePassword")
    @Operation(summary = "根据传入的User数据修改用户信息", description = "根据传入的User数据修改用户信息,manager权限可以任意修改，其他用户只能修改自己的信息")
    public Result<String> changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) throws NoSuchAlgorithmException {
        User loginUser = userService.getUserById((long) session.getAttribute("Id"));
        String oldMD5 = MD5Utils.toMD5(request.getOldPassword());
        if (!oldMD5.equals(loginUser.getPassword())) { System.out.println("旧密码错误"); return Result.error("-2", "密码错误");}
        int result = userService.updatePassword(loginUser, request.getNewPassword());
        if (result == 0) {
            return Result.error("-2", "修改失败");
        } else {
            return Result.success("修改成功");
        }
    }

    @PostMapping("/changeUserInfo")
    public Result<String> changeUserInfo(@RequestBody User user, HttpSession session) throws NoSuchAlgorithmException {
        User loginUser = userService.getUserById((long) session.getAttribute("Id"));
        if (loginUser.getId() != (long) session.getAttribute("Id")) { return Result.error("-1", "用户不对应");}
        int result = userService.updateUserInfo(user);
        if (result == 0) {
            return Result.error("-2", "修改失败");
        } else {
            return Result.success("保存成功");
        }
    }

    @Setter
    @Getter
    @Schema(description = "用户类别请求参数")
    public static class ChangePasswordRequest {
        @Schema(description = "用户类别代码", example = "Normal")
        private String oldPassword;
        private String newPassword;
    }

    @Setter
    @Getter
    @Schema(description = "用户类别请求参数")
    public static class UserClassRequest {
        @Schema(description = "用户类别代码", example = "Normal")
        private int userClassCode;
    }

    @GetMapping("/checkSession")
    @Operation(summary = "检查session", description = "接受客户端请求，经过拦截器检测后如果没问题则返回值，否则在拦截器中返回error")
    public User checkSession(HttpSession session) {
        return getUserBySession(session);
    }

    private User getUserBySession(HttpSession session) {
        Object userName = session.getAttribute("LoginUser");
        return userService.getUserByName(userName.toString());
    }
}


