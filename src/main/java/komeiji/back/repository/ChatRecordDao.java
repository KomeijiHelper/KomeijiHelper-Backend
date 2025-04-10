package komeiji.back.repository;

import jakarta.transaction.Transactional;
import komeiji.back.entity.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import komeiji.back.entity.ChatRecord;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface ChatRecordDao extends JpaRepository<ChatRecord, Long> {
    List<ChatRecord> findByPatientName(String patientName);
    ChatRecord findById(String id);
}
