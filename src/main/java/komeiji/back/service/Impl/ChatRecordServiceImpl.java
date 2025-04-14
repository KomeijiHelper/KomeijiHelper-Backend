package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.dto.RankDTO;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.ChatRecordService;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.RedisUtils;
import org.springframework.stereotype.Service;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ChatRecordDao chatrecordDao;
    @Resource
    private UserDao userDao;

    @Override
    public int setScore(RankDTO rank, String consultantName) {
        int result = chatrecordDao.setScore(rank.getRank(), rank.getCid());
        //NOTICE 当修改成功后 需要重新计算平均分
        if(result != 0)
        {
            float avgScore = chatrecordDao.getAverageScore(rank.getCid());
            if(redisUtils.hasHashKey(RedisTable.ConsultantAvgScore,consultantName))
            {
                redisUtils.addHash(RedisTable.ConsultantAvgScore,consultantName,avgScore);
            }
            else{
                redisUtils.setHashKey(RedisTable.ConsultantAvgScore,consultantName,avgScore);
            }
        }
        return result;
    }

    @Override
    public Float getAvgScore(String consultantName) {
        if(redisUtils.hasHashKey(RedisTable.ConsultantAvgScore,consultantName)){
            return (Float)redisUtils.getHash(RedisTable.ConsultantAvgScore,consultantName);
        }
        else{
            return chatrecordDao.getAverageScore(consultantName);
        }
    }
}
