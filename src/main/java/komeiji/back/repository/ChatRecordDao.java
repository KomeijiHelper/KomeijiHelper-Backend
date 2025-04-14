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
    List<ChatRecord> findByConsultantName(String consultantName);

    @Modifying
    @Transactional
    @Query("update ChatRecord cr set cr.score = ?1 where cr.id = ?2")
    int setScore(@Param("1") int score, @Param("2") String id);

//    @Modifying
    @Transactional
    @Query("select avg(cr.score) from ChatRecord cr where cr.consultantName = ?1 and cr.score > 0")//获取咨询师评分平均值
    Float getAverageScore(@Param("1")String consultantName);
}
