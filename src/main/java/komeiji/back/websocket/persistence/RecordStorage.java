package komeiji.back.websocket.persistence;

import java.util.List;

public interface RecordStorage {
   int singleStorage(MessageRecord record);

   int batchStorage(List<MessageRecord> records);

   void close();
}
