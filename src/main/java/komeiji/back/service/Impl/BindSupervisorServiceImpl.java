package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.ConToSup;
import komeiji.back.entity.User;
import komeiji.back.repository.ConToSupDao;
import komeiji.back.service.BindSupervisorService;
import org.springframework.stereotype.Service;

@Service
public class BindSupervisorServiceImpl implements BindSupervisorService {
    @Resource
    private ConToSupDao conToSupDao;


    @Override
    public int bindSupervisor(User supervisor,User consultant) {
        if(supervisor==null||consultant==null){
            return 0;
        }

        if(conToSupDao.findByConsultantId(consultant.getId())==null){
            ConToSup contosup = new ConToSup(consultant.getId(),supervisor.getId());
            conToSupDao.save(contosup);
            return 1;
        }
        else{
            return conToSupDao.updateSupervisorId(consultant.getId(),supervisor.getId());
        }
    }

    @Override
    public ConToSup checkBind(User consultant) {
        long id = consultant.getId();
        return conToSupDao.findByConsultantId(id);
    }
}
