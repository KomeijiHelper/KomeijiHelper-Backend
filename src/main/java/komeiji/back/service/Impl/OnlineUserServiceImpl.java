package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.UserClass;
import komeiji.back.service.OnlineUserService;
import komeiji.back.utils.RedisUtils;
import komeiji.back.utils.RedisTable;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    @Resource
    private RedisUtils redisUtils;
    @Override
    public Set<Object> getOnlineUsers(UserClass cla) {
        Set<Object> result = null;
        switch(cla){
            case Normal:
                result = redisUtils.getHashKeys(RedisTable.onlineNormal);
                break;
            case Assistant:
                result = redisUtils.getHashKeys(RedisTable.onlineConsultant);
                break;
            case Supervisor:
                result = redisUtils.getHashKeys(RedisTable.onlineSupervisor);
                break;
        }

        return result;

    }


}
