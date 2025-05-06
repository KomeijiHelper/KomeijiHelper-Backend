package komeiji.back.repository;

import jakarta.transaction.Transactional;
import komeiji.back.entity.ConToSup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ConToSupDao extends JpaRepository<ConToSup, Long> {
    ConToSup findByConsultantId(Long consultantId);


    @Modifying
    @Transactional
    @Query("update ConToSup set supervisorId =?2 where consultantId =?1")
    int updateSupervisorId(@Param("1")Long consultantId, @Param("2")Long supervisorId);


}
