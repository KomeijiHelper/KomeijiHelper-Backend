package komeiji.back.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;
import komeiji.back.dto.ConToSupDTO;
import komeiji.back.entity.ConToSup;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.UserDao;
import komeiji.back.service.BindSupervisorService;
import komeiji.back.utils.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bind_supervisor")
public class BindSupervisorController {
    @Resource
    private BindSupervisorService bindSupervisorService;
    @Resource
    private UserDao userDao;

    @PostMapping("/consultant/bind")
    public Result bindSupervisor(@RequestBody ConToSup contosup, HttpSession session){
        long conId = (long)session.getAttribute("Id");
        User con_user = userDao.findById(conId);
        if(con_user == null || (con_user.getUserClass() != UserClass.Assistant ))
        {
            return Result.error("407","身份错误，请联系管理员");
        }

        long supervisorId = contosup.getSupervisorId();
        User sup_user = userDao.findById(supervisorId);
        int result = bindSupervisorService.bindSupervisor(sup_user,con_user);
        if(result == 0){
            return Result.error("408","绑定失败，请联系管理员");
        }
        return Result.success();
    }

    @GetMapping("/consultant/check")
    public Result checkSupervisor(HttpSession session) {
        long conId = (long) session.getAttribute("Id");
        User con_user = userDao.findById(conId);
        ConToSup contosup = bindSupervisorService.checkBind(con_user);

        if(contosup == null){
            return Result.error("407","未绑定");
        }
        else{
            User sup_user = userDao.findById((long)contosup.getSupervisorId());
            return Result.success(sup_user.getUserName());
        }
    }

    @PostMapping("/manager/bind")
    public Result bindSupervisorByManager(@RequestBody ConToSupDTO dto, HttpSession session){
        long conId = (long) session.getAttribute("Id");
        User manager = userDao.findById(conId);
        if(manager == null || (manager.getUserClass() != UserClass.Manager ))
        {
            return Result.error("407","权限不足");
        }
        User con_user = userDao.findByUserName(dto.getConName());
        User sup_user = userDao.findByUserName(dto.getSupName());

        int result = bindSupervisorService.bindSupervisor(sup_user,con_user);
        if(result == 0){
            return Result.error("407","绑定失败");
        }
        return Result.success();
    }
}
