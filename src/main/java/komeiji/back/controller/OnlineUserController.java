package komeiji.back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.controller.UserController.UserClassRequest;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.UserClass;
import komeiji.back.entity.YCWConsultant;
import komeiji.back.service.OnlineUserService;
import komeiji.back.service.UserService;
import komeiji.back.utils.Result;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/online")
public class OnlineUserController {
    Gson gson = new Gson();
    @Resource
    private UserService userService;
    @Resource
    private OnlineUserService onlineUserService;

    @PostMapping("/getUser")
    public Result getUser(@RequestBody UserClassRequest cla, HttpServletResponse response, HttpSession session) throws IOException {
        Set<Object> result =  onlineUserService.getOnlineUsers(UserClass.fromCode(cla.getUserClassCode()));
        System.out.println(result);
        result.remove("-1");

        //NOTICE result中存储着在线用户的userName

        if(cla.getUserClassCode() != UserClass.Assistant.getCode())
        // 请求的不是咨询师
        {
            if(cla.getUserClassCode() == UserClass.Supervisor.getCode())
            {
                List<Object> supervisors = onlineUserService.getSupervisors(result);
                return Result.success(supervisors);
            }
            return Result.success(result.stream().toList());
        }
        //请求的是咨询师
        else{
            //NOTICE 从redis和数据库中获取在线用户
            List<Object> consultants = onlineUserService.getConsultants(result);
            List<YCWConsultant> newConsultants = new ArrayList<>();
            for (int i = 0; i < consultants.size(); i++) {
                Object c = consultants.get(i);
                Consultant consultant = new ObjectMapper().convertValue(c, Consultant.class);
                YCWConsultant kore = new YCWConsultant(consultant, userService.getUserById(consultant.getConsultantId()).getNickName());
                newConsultants.add(i, kore);
            }
            return Result.success(gson.toJson(newConsultants));
        }
    }
}
