package komeiji.back.websocket.persistence.impl;

import komeiji.back.websocket.persistence.MessageRecord;
import komeiji.back.websocket.persistence.RecordStorage;

import java.util.List;

public class MockRecordstorage implements RecordStorage {

    @Override
    public int singleStorage(MessageRecord record) {
        return 0;
    }

    @Override
    public int batchStorage(List<MessageRecord> records) {
        return 0;
    }

    @Override
    public void close() {

    }
}
