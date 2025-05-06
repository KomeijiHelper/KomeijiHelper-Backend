package komeiji.back.service;

import komeiji.back.dto.RankDTO;
import komeiji.back.entity.ChatRecord;

import java.util.List;
import java.util.Map;

public interface ChatRecordService {
    int setScore(RankDTO rank, String consultantName);
    Float getAvgScore(String consultantName);
    Map<String,Object> getTempChat(String CID);
    Boolean verifySupervisor(String consultantName, String supervisorName);
    List<ChatRecord> getHistory(String userName);
}
