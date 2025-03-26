package komeiji.back.service.Impl;

import com.google.gson.Gson;
import komeiji.back.service.ConsultService;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.MessageFactory;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.session.SessionToken;
import org.springframework.stereotype.Service;

import static komeiji.back.websocket.utils.Utils.sendMessageInUserSession;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Map;


@Service
public class ConsultServiceImpl implements ConsultService {
    private final static Gson gson = new Gson();

    @Override
    public void conenctRequest_Service(SessionToken patient_sessiontoken, SessionToken consultant_sessiontoken, String patient_name, String consultant_name) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        // TODO 向patient和consultant发送websocket消息，类型为MessageType.CONNECT 需要明确to_patient to_consultant的具体内容
        //TODO 用MD5算法将UID 进行转换 并将转换后的数据通过 to_patient to_consultant 发送给patient和consultant


        MessageDigest md = MessageDigest.getInstance("MD5");

        md.update(patient_name.getBytes("UTF-8"));
        String patient_md = HexFormat.of().formatHex(md.digest());
        md.update(consultant_name.getBytes("UTF-8"));
        String consultant_md = HexFormat.of().formatHex(md.digest());

        String to_patient = gson.toJson(Map.of("from",patient_md,"to",consultant_md));
        String to_consultant = gson.toJson(Map.of("from",consultant_md,"to",patient_md));

        Message ToPatient = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,consultant_sessiontoken,patient_sessiontoken,to_patient);
        Message ToConsultant = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,patient_sessiontoken,consultant_sessiontoken,to_consultant);

        sendMessageInUserSession(ToPatient);
        sendMessageInUserSession(ToConsultant);

    }
}
