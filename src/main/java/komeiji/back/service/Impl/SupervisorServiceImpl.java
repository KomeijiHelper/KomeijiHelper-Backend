package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.ConToSup;
import komeiji.back.entity.User;
import komeiji.back.repository.ConToSupDao;
import komeiji.back.service.SupervisorService;
import org.springframework.stereotype.Service;

@Service
public class SupervisorServiceImpl implements SupervisorService {
    @Resource
    private ConToSupDao conToSupDao;


    @Override
    public void bindSupervisor(User supervisor,User consultant) {


    }
}
