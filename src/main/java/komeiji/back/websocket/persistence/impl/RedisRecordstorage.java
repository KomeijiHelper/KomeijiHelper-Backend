package komeiji.back.websocket.persistence.impl;

import jakarta.annotation.Resource;
import komeiji.back.utils.BeanUtils;
import komeiji.back.utils.RedisUtils;
import komeiji.back.websocket.persistence.MessageRecord;
import komeiji.back.websocket.persistence.RecordStorage;
import komeiji.back.websocket.persistence.meta.Meta;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.List;
import java.util.UUID;

public class RedisRecordstorage implements RecordStorage {

    private final RedisUtils redisUtils = BeanUtils.getBean(RedisUtils.class);

    private UUID uuid;
    private Meta meta;

    @Override
    public int singleStorage(MessageRecord record) {
        System.out.println("_________-singleStorage-_________"+uuid.toString());
        redisUtils.rpush(uuid.toString(),record);
        return 0;

    }

    @Override
    public int batchStorage(List<MessageRecord> records) {
        System.out.println("_________-singleStorage-_________"+uuid.toString());
        for(int i = 0;i<records.size();i++){
            redisUtils.rpush(uuid.toString(),records.get(i));
        }
        return 0;
    }

    @Override
    public void close()  {
        if(!redisUtils.hasKey(uuid.toString())){
            System.out.println("Key does not exist");
            return;
        }
        System.out.println("_________-close-_________");
        String filePath = this.meta.getStorePath();

        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(filePath),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        JSONObject obj=new JSONObject();//创建JSONObject对象

        Object meta = redisUtils.lpop(uuid.toString());
        long sz = redisUtils.getListSize(uuid.toString());
        for(long i = 0;i<sz;i++)
        {
            JSONObject subObj=new JSONObject();//创建对象数组里的子对象
            subObj.put(String.valueOf(i),redisUtils.lpop(uuid.toString()));
            obj.accumulate("Message",subObj);
        }
        obj.put("Meta",meta);

        try {
            osw.write(obj.toString());
            osw.flush();//清空缓冲区，强制输出数据
            osw.close();//关闭输出流
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void setMeta(Meta metadata) {
       this.meta = metadata;
       this.uuid = metadata.getUuid();
       redisUtils.lpush(uuid.toString(),this.meta);
    }
}
