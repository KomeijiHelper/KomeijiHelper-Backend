package komeiji.back.service;

import komeiji.back.entity.ConToSup;
import komeiji.back.entity.User;

public interface BindSupervisorService {
    int bindSupervisor(User supervisor,User consultant);
    ConToSup checkBind(User consultant);
}
