package komeiji.back.controller;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.UserClass;
import komeiji.back.service.UserService;
import komeiji.back.entity.User;
import komeiji.back.utils.RedisUtils;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;
import komeiji.back.utils.Result;
import jakarta.annotation.Resource;

import java.io.*;
import java.util.ArrayList;
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

    @Resource
    private RedisUtils redisUtils;

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

        Boolean loginResult = userService.loginService(loginUser.getUserName(), loginUser.getPassword());

        if(loginResult){
            session.setAttribute("LoginUser", loginUser.getUserName());
            session.setAttribute("id",loginUser.getId())
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
    public String test() throws IOException {
        List<Person> persons = new ArrayList<>();
        persons.add(new Person("cjw", 25));
        persons.add(new Person("lxy", 23));
        persons.add(new Person("zxy", 22));
        persons.add(new Person("zxy", 22));

        for(int i = 0;i<persons.size();i++){
            redisUtils.rpush("persons", persons.get(i));
        }
        Object cjw = redisUtils.lpop("persons");
        System.out.println(cjw);
        System.out.println(cjw.getClass());
        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream("E:\\coding\\workspace\\Test\\1.json"),"UTF-8");

        return cjw.toString();

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
        Object userName = session.getAttribute("LoginUser");
        User user = userService.getUserByName(userName.toString());
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
    public Result<List<String>> getUsersByClass(@RequestBody UserClassRequest userClassRequest, HttpServletResponse response) throws IOException {
        UserClass userClass = UserClass.fromCode(userClassRequest.userClassCode);
        List<User> users = userService.getUsersByUserClass(userClass);
        List<String> names = users.stream()
                .map(User::getUserName)
                .toList();
        return users.isEmpty()
                ? Result.error(401, "User Not Found", response)
                : Result.success(names);
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
        Object userName = session.getAttribute("LoginUser");
        User user = userService.getUserByName(userName.toString());
        return user.getUserClass().getCode();
    }
}

 class Person{
   String name;
   int age;
   public Person(){
       this.name = "";
       this.age = 0;
   }
   public Person(String name,int age){
       this.name=name;
       this.age=age;
   }
 }