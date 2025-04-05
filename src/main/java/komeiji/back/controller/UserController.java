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
import komeiji.back.service.UserService;
import komeiji.back.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.*;
import komeiji.back.utils.Result;
import jakarta.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "用户基本功能", description = "用户基本功能接口")
@RestController
@RequestMapping(path ="/user")
public class UserController {
    @Resource
    private UserService userService;

    public static HashMap<String,HttpSession> sessions = new HashMap<>();

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "接受post请求体body中包含用户名和密码，返回登录成功的用户名")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = Result.class))),
                    @ApiResponse(responseCode = "402", description = "账号或密码错误", content = @Content(schema = @Schema(implementation = Result.class)))
            }
    )
    public Result<String> loginController(@RequestBody User loginUser, HttpSession session, HttpServletResponse response) throws IOException {
        System.out.println("用户名:"+loginUser.getUserName()+loginUser.getPassword());

        User loginResult = userService.loginService(loginUser.getUserName(), loginUser.getPassword());

        if(loginResult!= null){
            session.setAttribute("LoginUser", loginUser.getUserName());
            session.setAttribute("Id", loginResult.getId());

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
    public Result<String> registerController(@RequestBody User newUser, HttpSession session, HttpServletResponse response) throws IOException {
        Boolean registerResult = userService.registerService(newUser);
        if (registerResult) {
            session.setAttribute("LoginUser", newUser.getUserName());
            session.setAttribute("Id", newUser.getId());
            return Result.success(newUser.getUserName(), "注册成功");
        } else {
            return Result.error(456, "注册失败", response);
        }
    }

    @GetMapping("/logout")
    @Operation(summary = "用户登出", description = "用户登出，清除session")
    public void loginOut(HttpSession session) {
        session.removeAttribute("LoginUser");
        session.invalidate();
    }

    @GetMapping("/test")
    @Operation(summary = "测试接口", description = "测试接口")
    public String test() { return "test"; }

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
                    String filedName = f.getName();
                    return filedName.equals("password")
                            || filedName.equals("id")
                            || filedName.equals("userClass")
                            || filedName.equals("email");
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
                    return false;
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


    @PostMapping("/changeInfo")
    @Operation(summary = "根据传入的User数据修改用户信息", description = "根据传入的User数据修改用户信息,manager权限可以任意修改，其他用户只能修改自己的信息")
    public Result<String> changeInfo(@RequestBody User user,HttpSession session) {
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


    @Setter
    @Getter
    @Schema(description = "用户类别请求参数")
    public static class UserClassRequest {
        @Schema(description = "用户类别代码", example = "Normal")
        private int userClassCode;
    }

    @GetMapping("/checkSession")
    @Operation(summary = "检查session", description = "接受客户端请求，经过拦截器检测后如果没问题则返回值，否则在拦截器中返回error")
    public int checkSession(HttpSession session) {
        return getUserBySession(session).getUserClass().getCode();
    }

    private User getUserBySession(HttpSession session) {
        Object userName = session.getAttribute("LoginUser");
        return userService.getUserByName(userName.toString());
    }
}
