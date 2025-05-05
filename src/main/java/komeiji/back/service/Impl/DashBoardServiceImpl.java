package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.DashBoardService;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DashBoardServiceImpl implements DashBoardService {
    @Resource
    ChatRecordDao chatRecordDao;
    @Resource
    UserDao userdao;
    @Resource
    RedisUtils redisUtils;

    @Override
    public int getOneDayTotalRecord(User user, String date) {
        //date格式为yyyy-MM-dd格式

        String start = date + " 00:00:00";
        String end = date + " 23:59:59";

        //NOTICE 如果user为null 代表获取date当天所有的记录
        if (user == null) {
            return chatRecordDao.getOneDayTotalRecord(start, end,null);
        }

        //NOTICE user不为null 代表获取user当天的记录总数
        return chatRecordDao.getOneDayTotalRecord(start, end,user.getUserName());

    }

    @Override
    public int getPeriodTotalRecord(User user, String startDate, String endDate) {
        String start = startDate + " 00:00:00";
        String end = endDate + " 23:59:59";

        //NOTICE 如果user为null 代表获取date当天所有的记录
        if(user == null)
            return chatRecordDao.getOneDayTotalRecord(startDate, endDate,null);

        return chatRecordDao.getOneDayTotalRecord(startDate, endDate,user.getUserName());

    }

    @Override
    public Map<String,Integer> getUserCount() {
        int normal_total = userdao.getUserCount(UserClass.Normal);
        int consultant_total = userdao.getUserCount(UserClass.Assistant);
        int supervisor_total = userdao.getUserCount(UserClass.Supervisor);

        return Map.of("Normal",normal_total,"Assistant",consultant_total,"Supervisor",supervisor_total);
    }

    @Override
    public Map<String, Long> getOnlineUserCount() {
        Long normal_total = redisUtils.getHashSize(RedisTable.onlineNormal) - 1;
        Long consultant_total = redisUtils.getHashSize(RedisTable.onlineConsultant) - 1;
        Long supervisor_total = redisUtils.getHashSize(RedisTable.onlineSupervisor) - 1;
        return Map.of("Normal", normal_total, "Assistant", consultant_total, "Supervisor", supervisor_total);
    }

    @Override
    public Map<String, Integer> getLoginUserCount() {
        int normal_total = 0;
        int consultant_total = 0;
        int supervisor_total = 0;

        Set<Object> login_users = redisUtils.getSetMembers(RedisTable.loginUser);
        login_users.remove("-1");

        for (Object user : login_users) {
            System.out.println(user);
            User u = userdao.findByUserName((String) user);
            switch (u.getUserClass()) {
                case Normal:
                    normal_total++;
                    break;
                case Assistant:
                    consultant_total++;
                    break;
                case Supervisor:
                    supervisor_total++;
                    break;
            }
        }

        return Map.of("Normal", normal_total, "Assistant", consultant_total, "Supervisor", supervisor_total);
    }

}
