package komeiji.back.repository;

import komeiji.back.entity.ConToSup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConToSupDao extends JpaRepository<ConToSup, Long> {
    ConToSup findByConsultantId(Long consultantId);
    List<ConToSup> findBySupervisorId(Long supervisorId);
}
