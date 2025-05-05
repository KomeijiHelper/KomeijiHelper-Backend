package komeiji.back.service;

import komeiji.back.entity.User;

import java.util.List;
import java.util.Map;

public interface DashBoardService {
    int getOneDayTotalRecord(User user , String date);
    int getPeriodTotalRecord(User user, String startDate, String endDate);
    Map<String,Integer> getUserCount();
    Map<String,Long> getOnlineUserCount();
    Map<String,Integer> getLoginUserCount();
}
