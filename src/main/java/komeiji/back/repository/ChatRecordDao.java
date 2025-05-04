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

    //NOTICE 获取chatRecord总数
    @Transactional
    @Query("select count(*) from ChatRecord cr where cr.consultantName = ?1")
    Integer countByConsultantName(@Param("1") String consultantName);

    //NOTICE 获取评价过的chatRecord总数
    @Transactional
    @Query("select count(*) from ChatRecord cr where cr.consultantName = ?1 and cr.score > 0")
    Integer countByConsultantNameAndScore(@Param("1") String consultantName);

    //NOTICE 获取用户的所有ChatRecord
    @Transactional
    @Query("select cr from ChatRecord cr where cr.patientName = ?1 or cr.consultantName = ?1")
    List<ChatRecord> getAllChatRecordByUserName(@Param("1") String userName);

    @Transactional
    @Query("select count(cr) from ChatRecord cr where cr.timeStamp >= ?1 and cr.timeStamp <= ?2 and (?3 is null or cr.consultantName = ?3)")
    int getOneDayTotalRecord(@Param("1")String start,@Param("2")String end,@Param("3")String consultantName);



}
