package komeiji.back.service.Impl;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.annotation.Resource;
import komeiji.back.dto.RankDTO;
import komeiji.back.entity.ChatRecord;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.ConsultantDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.ChatRecordService;
import komeiji.back.utils.RedisTable;
import komeiji.back.utils.RedisUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatRecordServiceImpl implements ChatRecordService {
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private ChatRecordDao chatrecordDao;
    @Resource
    private UserDao userDao;
    @Resource
    private ConsultantDao consultantDao;

    private static final Gson gson = new Gson();

    @Override
    public int setScore(RankDTO rank, String consultantName) {
        int result = chatrecordDao.setScore(rank.getRank(), rank.getCid());
        //NOTICE 当修改成功后 需要重新计算平均分
        if(result != 0)
        {
            User consultantUser = userDao.findByUserName(consultantName);
            if(consultantUser == null || consultantUser.getUserClass() != UserClass.Assistant){
                //NOTICE 如果consultant不是Assistant 不需要更新Consultant数据库中的数据
                return result;
            }
            ChatRecord crd = chatrecordDao.findById(rank.getCid());
            Float avgScore = chatrecordDao.getAverageScore(consultantName);
            //NOTICE 更新Consultant数据库中的数据
            consultantDao.updateAvgScore(consultantUser.getId(), avgScore);
            //NOTICE 更新Redis中的数据
            Consultant con = consultantDao.findByConsultantId(consultantUser.getId());
            if(redisUtils.hasHashKey(RedisTable.ConsultantInfo,consultantName))
            {
                redisUtils.setHashKey(RedisTable.ConsultantInfo,consultantName,con);
            }
            else{
                redisUtils.addHash(RedisTable.ConsultantInfo,consultantName,con);
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

    @Override
    public Map<String, Object> getTempChat(String CID) {
        List<Object> message = redisUtils.getList(CID);
        JsonObject json = new JsonObject();
        int sz = message.size();
        Object meta =  message.get(0);
        JsonArray jsonArray = new JsonArray();
        for(int i=1;i<sz;i++){
            jsonArray.add(gson.toJsonTree(message.get(i)));
        }
        json.add("meta", gson.toJsonTree(meta));
        json.add("message", jsonArray);
        Map<String,Object> result = gson.fromJson(json.toString(), Map.class);

        return result;
    }

    @Override
    public Boolean verifySupervisor(String consultantName,String supervisorName) {
       return true;
    }

    @Override
    public List<ChatRecord> getHistory(String userName) {
        return chatrecordDao.getAllChatRecordByUserName(userName);
    }
}
