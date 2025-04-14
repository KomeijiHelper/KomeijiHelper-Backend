package komeiji.back.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.UserClass;
import komeiji.back.service.OnlineUserService;
import komeiji.back.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import komeiji.back.controller.UserController.UserClassRequest;

import java.io.IOException;
import java.util.Set;

@RestController
@RequestMapping(path = "/online")
public class OnlineUserController {
    @Resource
    private OnlineUserService onlineUserService;

    @PostMapping("/getUser")
    public Result<Object> getUser(@RequestBody UserClassRequest cla, HttpServletResponse response, HttpSession session) throws IOException {
        Set<Object> result =  onlineUserService.getOnlineUsers(UserClass.fromCode(cla.getUserClassCode()));
        result.remove("-1");
        return Result.success(result,"成功");
    }
}
