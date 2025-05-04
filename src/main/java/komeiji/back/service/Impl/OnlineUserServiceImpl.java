package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ConsultantDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.OnlineUserService;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class OnlineUserServiceImpl implements OnlineUserService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserDao userDao;

    @Resource
    private ConsultantDao consultantDao;

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

    @Override
    public List<Object> getConsultants(Set<Object> result) {
        List<Object> consultants = new ArrayList<>();
       for(Object conName : result)
       {
           System.out.println(conName.toString());
           if(redisUtils.hasHashKey(RedisTable.ConsultantInfo,conName.toString())){
               consultants.add(redisUtils.getHash(RedisTable.ConsultantInfo,conName.toString()));
           }
           else{
               //NOTICE 没有在缓存中获取到对应信息 需要从数据库中获取并更新到Redis中
               Consultant con = consultantDao.findByConsultantName(conName.toString());
               redisUtils.addHash(RedisTable.ConsultantInfo,conName.toString(),con);
               consultants.add(con);
           }

       }
       return consultants;
    }

    @Override
    public List<Object> getSupervisors(Set<Object> result) {
        List<Object> supervisors = new ArrayList<>();
       for(Object supName : result)
       {
           User sup =  userDao.findByUserName(supName.toString());
           supervisors.add(new Consultant(sup.getId(),sup.getUserName(),0,0,0));
       }

       return supervisors;
    }


}
