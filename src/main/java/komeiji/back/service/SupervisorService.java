package komeiji.back.service;

import komeiji.back.entity.User;
import org.springframework.stereotype.Service;

public interface SupervisorService {
    void bindSupervisor(User supervisor,User consultant);
}
