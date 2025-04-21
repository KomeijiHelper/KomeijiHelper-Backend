package komeiji.back.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.UserClass;
import komeiji.back.service.OnlineUserService;
import komeiji.back.utils.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import komeiji.back.controller.UserController.UserClassRequest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/online")
public class OnlineUserController {
    @Resource
    private OnlineUserService onlineUserService;

    @PostMapping("/getUser")
    public Result getUser(@RequestBody UserClassRequest cla, HttpServletResponse response, HttpSession session) throws IOException {
        Set<Object> result =  onlineUserService.getOnlineUsers(UserClass.fromCode(cla.getUserClassCode()));
        System.out.println(result);
        result.remove("-1");

        //NOTICE result中存储着在线用户的userName
        if(cla.getUserClassCode() != UserClass.Assistant.getCode())
        {
            return Result.success(result.stream().toList());
        }
        else{
            //NOTICE 从redis和数据库中获取在线用户
            List<Object> consultants = onlineUserService.getConsultants(result);
            System.out.println(consultants);
            return Result.success(consultants);
        }
    }
}
