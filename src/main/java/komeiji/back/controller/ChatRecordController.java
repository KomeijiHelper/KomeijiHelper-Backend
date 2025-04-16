package komeiji.back.controller;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.dto.RankDTO;
import komeiji.back.entity.ChatRecord;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.ChatRecordService;
import komeiji.back.utils.RedisUtils;
import komeiji.back.utils.Result;
import komeiji.back.utils.RedisTable;


import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/chatRecord")
public class ChatRecordController {

    @Resource
    UserDao userdao;
    @Resource
    RedisUtils redisUtils;
    @Resource
    ChatRecordDao chatrecordDao;
    @Resource
    ChatRecordService chatrecordService;

    private static final Gson gson = new Gson();

    @PostMapping("/rating")
    public Result recordRating(@RequestBody RankDTO rankdto, HttpSession session, HttpServletResponse response) {
        if (rankdto.getApprove() == 0) {
            return Result.success("取消评价");
        }
        System.out.println(rankdto.toString());

        String userName = (String) session.getAttribute("LoginUser");
        User loginUser = userdao.findByUserName(userName);

        //NOTICE 当RankDto中的CID为null时 需要从redis中获取CID
        if (rankdto.getCid() == null) {
            String key = RedisTable.PatientTempScore + loginUser.getUserName();
            if (!redisUtils.hasKey(key)) {
                return Result.error("405", "未找到临时评分记录");
            }

            //NOTICE 在此处进行评分
            String CID = (String) redisUtils.get(key);
            rankdto.setCid(CID);
            ChatRecord crd = chatrecordDao.findById(rankdto.getCid());
            int result = chatrecordService.setScore(rankdto, crd.getConsultantName());
            if (result == 0) {
                return Result.error("406", "评分失败");
            } else {
                return Result.success("评分成功");
            }
        }

        //NOTICE 当RankDto中的CID不为null时 说明是通过历史记录进行评分
        else {
            //NOTICE 需要验证patient与loginUser是否匹配
            ChatRecord crd = chatrecordDao.findById(rankdto.getCid());
            if (crd == null) {
                return Result.error("407", "未找到该聊天记录");
            }

            if (!crd.getPatientName().equals(loginUser.getUserName())) {
                //评分的用户与record的用户不匹配
                return Result.error("408", "用户不匹配");
            }
            //NOTICE 在此处进行评分
            int result = chatrecordService.setScore(rankdto, crd.getConsultantName());
            if (result == 0) {
                return Result.error("406", "评分失败");
            } else {
                return Result.success("评分成功");
            }
        }
    }

    @GetMapping("/getRating")
    public Result getRating(@Param("consultantName") String consultantName, HttpSession session, HttpServletResponse response) {
//        System.out.println(chatrecordDao.findByConsultantName(consultantName));
        if (chatrecordDao.findByConsultantName(consultantName) == null) {
            return Result.error("409", "该咨询师暂无评价");
        }
        Float avgScore = chatrecordService.getAvgScore(consultantName);
        if (avgScore == null) {
            return Result.error("409", "该咨询师暂无评价");
        }
        return Result.success(avgScore);
    }

    @GetMapping("/downloadFile")
    public Result uploadFile(@Param("fileId") String fileId, HttpSession session, HttpServletResponse response) throws UnsupportedEncodingException {
        //TODO 上传聊天文件
        String UserName = (String) session.getAttribute("LoginUser");
        User loginUser = userdao.findByUserName(UserName);
        ChatRecord crd = chatrecordDao.findById(fileId);

        if (crd == null) {
            return Result.error("409", "未找到该聊天记录");
        }

        if (!crd.getConsultantName().equals(UserName) && !crd.getPatientName().equals(loginUser.getUserName()) && !loginUser.getUserClass().equals(UserClass.Manager)) {
            return Result.error("410", "无权下载该文件");
        }

        File file = new File(crd.getFilePath());

        if (!file.exists()) {
            return Result.error("411", "文件不存在");
        }

        // 清空 response
        response.reset();
        response.setCharacterEncoding("UTF-8");

        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(file.getName(), "UTF-8"));
        response.setContentType("application/octet-stream");

        // 将文件读到输入流中
        try (InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath()))) {

            OutputStream outputStream = new BufferedOutputStream(response.getOutputStream());

            byte[] buffer = new byte[1024];
            int len;

            //从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
            while ((len = is.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            outputStream.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success();
    }

    @GetMapping("/getTempChat")
    public Result getTempChat(@Param("consultantName") String consultantName, HttpSession session, HttpServletResponse response) {
        //DONE 获取临时聊天记录
        //TODO 需要做consultant与Supervisor的身份验证

        String loginUserName = (String) session.getAttribute("LoginUser");
        if(!chatrecordService.verifySupervisor(consultantName,loginUserName))
        {
            return Result.error("411", "无权查看该咨询师的临时聊天记录");
        }

        String CID = (String) redisUtils.getHash(RedisTable.UserToSession,consultantName);
//        System.out.println("CID = " + CID);
        if (CID == null) {
            return Result.error("412", "未找到临时聊天记录");
        }

        if(!redisUtils.hasHashKey(RedisTable.SessionToUser,CID))
        {
            return Result.error("413", "未找到该聊天记录");
        }

        Map<String,Object> map = chatrecordService.getTempChat(CID);
        return Result.success(map);
    }



}
