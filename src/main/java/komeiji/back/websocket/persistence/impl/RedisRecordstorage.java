package komeiji.back.websocket.persistence.impl;

import jakarta.annotation.Resource;
import komeiji.back.utils.RedisUtils;
import komeiji.back.websocket.persistence.MessageRecord;
import komeiji.back.websocket.persistence.RecordStorage;

import java.util.List;
import java.util.UUID;

public class RedisRecordstorage implements RecordStorage {
    @Resource
    private RedisUtils redisUtils;

    private UUID uuid;

    public void set_Uuid(UUID uuid){
        this.uuid = uuid;
        redisUtils.lpush(uuid.toString(), uuid.toString()+System.currentTimeMillis());
    }
    @Override
    public int singleStorage(MessageRecord record) {
        redisUtils.rpush(uuid.toString(),record);
        return 0;

    }

    @Override
    public int batchStorage(List<MessageRecord> records) {
        for(int i = 0;i<records.size();i++){
            redisUtils.rpush(uuid.toString(),records.get(i));
        }
        return 0;
    }

    @Override
    public void close() {

    }
}
