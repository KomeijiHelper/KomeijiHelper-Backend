package komeiji.back.websocket.persistence.impl;

import com.google.gson.*;
import komeiji.back.entity.ChatRecord;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.utils.BeanUtils;
import komeiji.back.utils.RedisUtils;
import komeiji.back.websocket.persistence.MessageRecord;
import komeiji.back.websocket.persistence.RecordStorage;
import komeiji.back.websocket.persistence.meta.Meta;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisRecordstorage implements RecordStorage {
    private static final Gson gson = new Gson();
    private final RedisUtils redisUtils = BeanUtils.getBean(RedisUtils.class);
    private final ChatRecordDao chatRecordDao = BeanUtils.getBean(ChatRecordDao.class);

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
        for (MessageRecord record : records) {
            redisUtils.rpush(uuid.toString(), record);
        }
        return 0;
    }

    // TODO: zip file
    private void dumpToFile() {
        if(!redisUtils.hasKey(uuid.toString())){
            System.out.println("Key does not exist");
            return;
        }
        System.out.println("_______________-close-________________");
        String filePath = this.meta.getStorePath();
//        ChatRecord crd = chatRecordDao.findById(uuid.toString());

        OutputStreamWriter osw = null;
        try {
            osw = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        JsonObject obj=new JsonObject();//创建JSONObject对象

        Object meta = redisUtils.lpop(uuid.toString());
        long sz = redisUtils.getListSize(uuid.toString());
        JsonArray messages = new JsonArray();
        for(long i = 0;i<sz;i++)
        {
            messages.add(gson.toJsonTree(redisUtils.lpop(uuid.toString())));
        }
        obj.add("meta",gson.toJsonTree(meta));
        obj.add("messages",messages);

        try {
            osw.write(obj.toString());
            osw.flush();//清空缓冲区，强制输出数据
            osw.close();//关闭输出流
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close()  {
        CompletableFuture<Void> future = CompletableFuture.runAsync(this::dumpToFile);
        future.whenComplete((result,ex)->{
          if(ex==null) {
            System.out.println("write completed");
          }
        });
    }

    @Override
    public void setMeta(Meta metadata) {
       this.meta = metadata;
       this.uuid = metadata.getUuid();
       redisUtils.lpush(uuid.toString(),this.meta);
    }
}
