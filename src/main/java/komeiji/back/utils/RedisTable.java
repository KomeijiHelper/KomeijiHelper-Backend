package komeiji.back.utils;

public class RedisTable {
    public static final String onlineNormal = "onlineNormal";  //hash
    public static final String onlineConsultant = "onlineConsultant"; //hash
    public static final String onlineSupervisor = "onlineSupervisor"; //hash

    public static final String SessionToUser = "SessionToUser";
    public static final String UserToSession = "UserToSession";

    private final RedisUtils redisUtils = BeanUtils.getBean(RedisUtils.class);

    public void initRedisTable(){
        if(!redisUtils.hasKey(onlineNormal)){
            redisUtils.addHash(onlineNormal,"-1",-1);
        }
        if(!redisUtils.hasKey(onlineConsultant)){
            redisUtils.addHash(onlineConsultant,"-1",-1);
        }
        if(!redisUtils.hasKey(onlineSupervisor)){
            redisUtils.addHash(onlineSupervisor,"-1",-1);
        }
        if(!redisUtils.hasKey(SessionToUser)){
            redisUtils.addHash(SessionToUser,"-1",-1);
        }
        if(!redisUtils.hasKey(UserToSession)){
            redisUtils.addHash(UserToSession,"-1",-1);
        }
    }


}
