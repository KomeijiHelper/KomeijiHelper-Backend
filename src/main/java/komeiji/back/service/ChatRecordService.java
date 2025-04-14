package komeiji.back.service;

import komeiji.back.dto.RankDTO;

public interface ChatRecordService {
    public int setScore(RankDTO rank,String consultantName);
    public Float getAvgScore(String consultantName);
}
