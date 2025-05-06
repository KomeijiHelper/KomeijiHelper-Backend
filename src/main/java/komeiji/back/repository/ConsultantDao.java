package komeiji.back.repository;

import jakarta.transaction.Transactional;
import komeiji.back.entity.Consultant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsultantDao extends JpaRepository<Consultant, Integer> {
    Consultant findByConsultantId(Long consultantId);
    Consultant findByConsultantName(String consultantName);

    //DONE 为totalRecord自增1

    @Modifying
    @Transactional
    @Query("update Consultant c set c.totalRecord = c.totalRecord + 1 where c.consultantId =?1")
    void addOneTotalRecord(@Param("1") Long consultantId);

    //DONE 为scoreRecord自增1 并更新avgScore
    @Modifying
    @Transactional
    @Query("update Consultant c set c.scoreRecord = c.scoreRecord + 1,c.avgScore = ?2 where c.consultantId =?1")
    void updateAvgScore(@Param("1") Long consultantId, @Param("2") float score);

}
