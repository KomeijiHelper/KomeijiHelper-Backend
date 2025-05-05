package komeiji.back.controller;

import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import komeiji.back.entity.enum_entity.ConsultRequestStatus;
import komeiji.back.service.ConsultService;
import komeiji.back.utils.RedisUtils;
import komeiji.back.utils.Result;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.MessageFactory;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.session.SessionToken;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static komeiji.back.websocket.utils.Utils.sendMessageInUserSession;

@Tag(name = "")
@RestController
@RequestMapping(path="/consult")
public class ConsultController {
    private volatile ConcurrentHashMap<String, CountDownLatch> waiters = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String,String> invite_map = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, ConsultRequestStatus> requestStatus_map = new ConcurrentHashMap<>();

    @Resource
    private ConsultService consult_Service_service;
    @Resource
    private ConsultService consultService;
    @Resource
    RedisUtils redisUtils;

    private static final Gson gson = new Gson();

    private void sendRejectMessage(String from,String to,String reason) {
        SessionToken from_session_token = new SessionToken(from);
        SessionToken to_session_token = new SessionToken(to);
        Message toSupervisor = MessageFactory.newTextMessage(MessageType.CHAT_REJECT,
                from_session_token,to_session_token,
                gson.toJson(Map.of("cancel_id",from,"reason",reason)),
                System.currentTimeMillis());
        sendMessageInUserSession(toSupervisor);
    }

    /**
     *
     * 用户调用connect_request接口，传入请求咨询师的id
     *
     * 获取对应咨询师的channel
     *
     * 向咨询师发送对应消息
     *
     * 等待咨询师回复 咨询师调用响应API接触用户线程的阻塞
     *      1.如果用户线程阻塞以及被remove 说明用户已取消预约，此时提醒咨询师
     *      2.如果用户线程阻塞未被remove，则等待用户回复，回复后更新数据库，并向用户发送回复消息
     *
     * 如果咨询师同意，则向双方channel发送可以进行连接的消息
     **/

    @GetMapping("/connect_request")
    public Result<String> connectRequest(@Param("consult_id") String consult_id, HttpSession session, HttpServletResponse response) throws IOException, InterruptedException, NoSuchAlgorithmException {
        //TODO 当前正在等待的用户不能重复预约 正在接受咨询的咨询师也不能进行接受
        String Login_user = (String)session.getAttribute("LoginUser");
        if(requestStatus_map.containsKey(Login_user)) {
            return Result.error("405","您已有其他请求，请等待处理");
        }

        if(!consultService.checkBusy(consult_id))
        {
            return Result.error("409","该咨询师忙碌，请稍后再试");
        }

        waiters.put(Login_user, new CountDownLatch(1));
        invite_map.put(Login_user, consult_id);
        requestStatus_map.put(Login_user, ConsultRequestStatus.WAITING);
//        System.out.println("after put waters:"+waiters);

        //TODO SessionToken的创建 需要进行修改 根据具体方法
        SessionToken patient_sessiontoken = new SessionToken(Login_user);
        SessionToken consultant_sessiontoken = new SessionToken(consult_id);

        //NOTICE 根据SessionToken 利用MessageFactory.newTextMessage发送消息   向对应的咨询师发送请求连接的信息
        ChatRequest chatRequest = new ChatRequest();
        chatRequest.setMessage("\"请求连接from:\""+Login_user);
        chatRequest.setPatientId(Login_user);
        Gson gson = new Gson();
        Message toConsultant = MessageFactory.newTextMessage(MessageType.CHAT_REQUEST,patient_sessiontoken,consultant_sessiontoken,gson.toJson(chatRequest), System.currentTimeMillis() / 1000);
        sendMessageInUserSession(toConsultant);

        //NOTICE 等待咨询师恢复
        CountDownLatch latch = waiters.get(Login_user);
        System.out.println("开始等待------------------");
        latch.await(40000, TimeUnit.MILLISECONDS);
        System.out.println("等待结束------------------");



        //NOTICE result返回处理结果 requestStatus_map记录当前请求被接受/拒绝/取消/超时的状态
        Result<String> result = new Result<>();
        switch(requestStatus_map.get(Login_user)){
            case WAITING:
                //NOTICE 超时未回复，提醒用户并返回
                result =  Result.error("408","请求超时，请重新预约");
                sendRejectMessage(Login_user,consult_id,"请求超时未处理");
                break;
            case ACCEPTED:
                //NOTICE 咨询师同意，建立连接 调用consultService
                consultService.conenctRequest_Service(patient_sessiontoken,consultant_sessiontoken,Login_user,consult_id);
                result = Result.success("成功建立连接");
                break;
            case REJECTED:
                //NOTICE 咨询师拒绝，提醒用户
                result = Result.error("407","咨询师拒绝了您的请求");
                break;
            case CANCELED:
                //NOTICE 用户取消预约，提醒用户
                result = Result.error("406","您已取消预约");
                break;
        }


        waiters.remove(Login_user);
        invite_map.remove(Login_user);
        requestStatus_map.remove(Login_user);

        return result;


    }

    @GetMapping("/cancel_request")
    public Result<String> cancelRequest(@RequestParam String consult_id, HttpSession session, HttpServletResponse response) throws IOException {
        String login_user = (String) session.getAttribute("LoginUser");
        CountDownLatch latch = waiters.get(login_user);
        if(latch == null){
           return Result.error("401","请求已被处理");
        }
        else{
            latch.countDown();
            requestStatus_map.replace(login_user, ConsultRequestStatus.CANCELED);
            System.out.println(requestStatus_map);
            sendRejectMessage(login_user,consult_id,"请求已被对方取消");
        }
        return Result.success("已处理");
    }

    @PostMapping("/response_request")
    public Result<String> responseRequest(@RequestBody ChatResponse chatResponse, HttpSession session, HttpServletResponse response) throws IOException, InterruptedException
    {
//        System.out.println("patient_id:"+patient_id);
//        System.out.println("waiter.size():"+waiters.size());
//        System.out.println("waiters:"+waiters);

        System.out.println("收到回应" + chatResponse.accept);
        Optional<CountDownLatch> latch = Optional.ofNullable(waiters.get(chatResponse.patientId));
        if(latch.isPresent() && requestStatus_map.get(chatResponse.patientId).equals(ConsultRequestStatus.WAITING)){
            //TODO 根据accept_reject 进行不同业务处
            if(chatResponse.accept){
                requestStatus_map.replace(chatResponse.patientId,ConsultRequestStatus.ACCEPTED);
            }
            else{
                requestStatus_map.replace(chatResponse.patientId,ConsultRequestStatus.REJECTED);
            }
            latch.get().countDown();
            return Result.success("请求已处理");
        }
        else{
            return Result.error("401","请求已过期");
        }
    }

    @Setter
    @Getter
    @Schema(description = "咨询师回应的Request Body")
    public static class ChatResponse {
        @Schema(description = "咨询者Id", example = "1")
        private String patientId;
        @Schema(description = "同意与否", example = "0")
        private boolean accept;
    }

    @Setter
    @Getter
    @Schema(description = "咨询者发起请求的Request Body")
    public static class ChatRequest {
        @Schema(description = "咨询者Id", example = "1")
        private String patientId;
        @Schema(description = "附加信息", example = "Hello")
        private String message;
    }
}


