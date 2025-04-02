package komeiji.back.websocket.persistence;

import komeiji.back.websocket.persistence.meta.Meta;

import java.util.List;

public interface RecordStorage {
   int singleStorage(MessageRecord record);

   int batchStorage(List<MessageRecord> records);

   void close();

   void setMeta(Meta metadata);
}
