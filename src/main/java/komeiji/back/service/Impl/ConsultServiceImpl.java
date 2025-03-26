package komeiji.back.service.Impl;

import komeiji.back.service.ConsultService;
import komeiji.back.websocket.message.Message;
import komeiji.back.websocket.message.MessageFactory;
import komeiji.back.websocket.message.MessageType;
import komeiji.back.websocket.session.SessionToken;
import org.springframework.stereotype.Service;

import static komeiji.back.websocket.utils.Utils.sendMessageInUserSession;

@Service
public class ConsultServiceImpl implements ConsultService {

    @Override
    public void conenctRequest_Service(SessionToken patient_sessiontoken, SessionToken consultant_sessiontoken, String patient_name, String consultant_name) {
        // TODO 向patient和consultant发送websocket消息，类型为MessageType.CONNECT 需要明确to_patient to_consultant的具体内容
        String to_patient = "";
        String to_consultant = "";

        Message ToPatient = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,consultant_sessiontoken,patient_sessiontoken,to_patient);
        Message ToConsultant = MessageFactory.newTextMessage(MessageType.CHAT_CONNECT,patient_sessiontoken,consultant_sessiontoken,to_consultant);

        sendMessageInUserSession(ToPatient);
        sendMessageInUserSession(ToConsultant);

    }
}
