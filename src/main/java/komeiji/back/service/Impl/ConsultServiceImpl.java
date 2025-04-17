package komeiji.back.service.Impl;

import com.google.gson.Gson;
import jakarta.annotation.Resource;
import komeiji.back.entity.ChatRecord;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.ConsultantDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.ConsultService;
import komeiji.back.utils.Result;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.MessageFactory;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.session.SessionToken;
import org.springframework.stereotype.Service;

import static komeiji.back.websocket.utils.Utils.sendMessageInUserSession;
import komeiji.back.websocket.persistence.ConversationUtils;
import komeiji.back.utils.RedisUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HexFormat;
import java.util.Map;
import java.util.UUID;

import komeiji.back.utils.RedisTable;

@Service
public class ConsultServiceImpl implements ConsultService {
    private final static Gson gson = new Gson();

    @Resource
    RedisUtils redisUtils;
    @Resource
    ChatRecordDao chatRecordDao;
    @Resource
    UserDao userdao;
    @Resource
    ConsultantDao consultantDao;

    @Override
    public void conenctRequest_Service(SessionToken patient_sessiontoken, SessionToken consultant_sessiontoken, String patient_name, String consultant_name) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // NOTICE 向patient和consultant发送websocket消息，类型为MessageType.CONNECT 需要明确to_patient to_consultant的具体内容
        //NOTICE 用MD5算法将UID 进行转换 并将转换后的数据通过 to_patient to_consultant 发送给patient和consultant
        //年月日时分秒
        //1
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = now.format(formatter2);
        System.out.println("format time :"+time);

        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(patient_name.getBytes("UTF-8"));
        String patient_md = HexFormat.of().formatHex(md.digest());
        md.update(consultant_name.getBytes("UTF-8"));
        String consultant_md = HexFormat.of().formatHex(md.digest());

        String patient_token = patient_md+time;
        String consultant_token = consultant_md+time;

        //TODO 需要区分是 患者——咨询师 还是 咨询师——督导
        StoreChatRecord(patient_token,consultant_token,consultant_name,patient_name,time);
        // patient和consultant的sessiontoken为userName加密后与timeStamp拼接而来

        String to_patient = gson.toJson(Map.of("from",patient_token,"to",consultant_token));
        String to_consultant = gson.toJson(Map.of("from",consultant_token,"to",patient_token));

        Message ToPatient = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,consultant_sessiontoken,patient_sessiontoken,to_patient,System.currentTimeMillis() / 1000);
        Message ToConsultant = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,patient_sessiontoken,consultant_sessiontoken,to_consultant,System.currentTimeMillis() / 1000);

        sendMessageInUserSession(ToPatient);
        sendMessageInUserSession(ToConsultant);

    }

    @Override
    public void rejectRequest_Service(SessionToken patient_sessiontoken) {
//        Message reject_patient = MessageFactory.newTextMessage(MessageType.)
    }




    public void StoreChatRecord(String patient_token,String consultant_token,String consultant_name,String patient_name,String time)
    {
        //TODO 将咨询师和患者作区分
       //TODO 增加咨询师和督导进行聊天时 将HelpSessionToUser UserToHelpSession中进行存储 + 数据库中对应的设置
        //NOTICE 可以不使用HelpSessionToUser 而是全部存储在SessionToUser中 这样可以统一找到对应的求助者是咨询师还是patient

        User patient = userdao.findByUserName(patient_name);
        UUID CID = ConversationUtils.sessionTokens2CID(new SessionToken(patient_token),new SessionToken(consultant_token));

        Map<String,String> map = Map.of("patient",patient_name,"consultant",consultant_name);//NOTICE 通过patient_token和consultant_token合成CID， 在Redis中将CID与patient_name / consultant_name对应 以便聊天记录的导出
        redisUtils.addHash(RedisTable.SessionToUser,CID.toString(),map); //NOTICE 无论是普通患者还是咨询师求助 都需要在SessionToUser中存储 区分的地方在UserToSession

        if(patient.getUserClass() == UserClass.Assistant){
            //NOTICE 在此处处理咨询师求助的逻辑
            //NOTICE 不同的点在于 咨询师和督导需要存储在UserToHelpSession中
            redisUtils.addHash(RedisTable.UserToHelpSession,patient_token,CID.toString());
            redisUtils.addHash(RedisTable.UserToHelpSession,consultant_token,CID.toString());

            //TODO 在websocket断开后 需要进行区分
        }
        else {
            //NOTICE 此处进行普通患者进行咨询的逻辑
            redisUtils.addHash(RedisTable.UserToSession, patient_name, CID.toString());
            redisUtils.addHash(RedisTable.UserToSession, consultant_name, CID.toString());
        }

        //NOTICE 在websocket连接断开后 去除对应RedisTable中的内容
        //NOTICE 在数据库中存储对应索引
        User consultantUser = userdao.findByUserName(consultant_name);
        chatRecordDao.save(new ChatRecord(CID.toString(),patient_name,consultant_name,consultantUser.getUserClass().getCode(),time,"chats/"+CID.toString()+".json"));
        if(consultantUser.getUserClass() == UserClass.Assistant){
            //NOTICE 更新Redis中咨询师具体数据 评分 总记录 评价记录
            consultantDao.addOneTotalRecord(consultantUser.getId());
            Consultant con = consultantDao.findByConsultantId(consultantUser.getId());

            if(redisUtils.hasHashKey(RedisTable.ConsultantInfo,consultant_name)){
                redisUtils.setHashKey(RedisTable.ConsultantInfo,consultant_name,con);
            }
            else{
                redisUtils.addHash(RedisTable.ConsultantInfo,consultant_name,con);
            }
        }

    }




}
