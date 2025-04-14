package komeiji.back.utils;

public class RedisTable {
    public static final String loginUser = "loginUser";
    public static final String onlineNormal = "onlineNormal";  //hash
    public static final String onlineConsultant = "onlineConsultant"; //hash
    public static final String onlineSupervisor = "onlineSupervisor"; //hash

    public static final String SessionToUser = "SessionToUser";
    public static final String UserToSession = "UserToSession";

    public static final String UserToHelpSession = "UserToHelpSession"; //NOTICE 记录咨询师和督导的对话CID

    public static final String PatientTempScore = "PatientTempScore_";

    public static final String ConsultantAvgScore = "ConsultantAvgScore"; //存储咨询师评分



    private static final RedisUtils redisUtils = BeanUtils.getBean(RedisUtils.class);

    public static void Init(){
        redisUtils.delete(loginUser);
        redisUtils.delete(onlineNormal);
        redisUtils.delete(onlineConsultant);
        redisUtils.delete(onlineSupervisor);
        redisUtils.delete(SessionToUser);
        redisUtils.delete(UserToSession);
        redisUtils.delete(UserToHelpSession);
        redisUtils.delete(ConsultantAvgScore);

        initRedisHashTable();
        initRedisSetTable();
    }

    public static void initRedisHashTable(){
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
        if(!redisUtils.hasKey(UserToHelpSession)){
            redisUtils.addHash(UserToHelpSession,"-1",-1);
        }
        if(!redisUtils.hasKey(ConsultantAvgScore)){
            redisUtils.addHash(ConsultantAvgScore,"-1",-1);
        }
    }

    public static void initRedisSetTable(){
        if(!redisUtils.hasKey(loginUser)){
            redisUtils.addSet("loginUser","-1");
        }
    }


}
