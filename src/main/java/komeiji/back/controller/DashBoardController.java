package komeiji.back.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import komeiji.back.dto.PeriodDTO;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ConsultantDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.DashBoardService;
import komeiji.back.utils.Result;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
public class DashBoardController {
    @Resource
    private DashBoardService dashBoardService;
    @Resource
    private UserDao userDao;
    @Resource
    private ConsultantDao consultantDao;


    //TODO: 管理员需要获取过去十四天每天的聊天记录总数
    @GetMapping("/consultant/chatRecordCount")
    public Result getChatRecordCount(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if(user.getUserClass() != UserClass.Assistant && user.getUserClass() != UserClass.Supervisor)
        {
            return Result.error("407","权限不足");
        }

        List<Integer> chatRecordCountList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            String dateStr = date.format(formatter);
            int count = dashBoardService.getOneDayTotalRecord(user,dateStr);
            chatRecordCountList.add(count);
        }
    return Result.success(chatRecordCountList);
    }

    @GetMapping("/consultant/getInfo")
    public Result getInfo(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if(user.getUserClass() != UserClass.Assistant)
        {
            return Result.error("407","权限不足");
        }
        Consultant info = consultantDao.findByConsultantId(user.getId());
        if(info == null){
            return Result.error("408","未找到该用户信息");
        }

        return Result.success(info);
    }

    @GetMapping("/manager/userCount")
    public Result getUserCount(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if (user.getUserClass() != UserClass.Manager) {
            return Result.error("407", "权限不足");
        }

        Map<String,Integer> userCountMap = dashBoardService.getUserCount();
        return Result.success(userCountMap);
    }
    @GetMapping("/manager/onlineUserCount")
    public Result getOnlineUserCount(HttpSession session) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if (user.getUserClass() != UserClass.Manager) {
            return Result.error("407", "权限不足");
        }
        Map<String,Integer> onlineUserCountMap = dashBoardService.getLoginUserCount();
        return Result.success(onlineUserCountMap);
    }

    @GetMapping("/consultant/period/chatRecordCount")
    public Result getPeriodChatRecordCount(HttpSession session,@RequestBody PeriodDTO period) {
        System.out.println(period);
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if(user.getUserClass() != UserClass.Assistant && user.getUserClass() != UserClass.Supervisor){
            return Result.error("407","权限不足");
        }

        int count = dashBoardService.getPeriodTotalRecord(user, period.getStart(), period.getEnd());

        return Result.success(count);
    }

    @PostMapping("/consultant/period/chatRecord")
    public Result getPeriodChatRecord(HttpSession session,@RequestBody PeriodDTO period) {
        User user = userDao.findByUserName((String) session.getAttribute("LoginUser"));
        if(user.getUserClass() != UserClass.Assistant && user.getUserClass() != UserClass.Supervisor)
        {
            return Result.error("407","权限不足");
        }

        List<Integer> chatRecordCountList = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate start = LocalDate.parse(period.getStart(), formatter);
        LocalDate end = LocalDate.parse(period.getEnd(), formatter);

        for (LocalDate date = start; !date.isAfter(end); date = date.plusDays(1)) {
            String formatted = date.format(formatter);
            int count = dashBoardService.getOneDayTotalRecord(user,formatted);
            chatRecordCountList.add(count);
        }
        return Result.success(chatRecordCountList);

    }



}
