package komeiji.back.service;

import komeiji.back.dto.RankDTO;
import komeiji.back.entity.ChatRecord;

import java.util.List;
import java.util.Map;

public interface ChatRecordService {
    public int setScore(RankDTO rank,String consultantName);
    public Float getAvgScore(String consultantName);
    public Map<String,Object> getTempChat(String CID);
    public Boolean verifySupervisor(String consultantName, String supervisorName);
//    public List<ChatRecord>
}
