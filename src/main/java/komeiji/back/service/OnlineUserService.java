package komeiji.back.service;

import komeiji.back.entity.Consultant;
import komeiji.back.entity.UserClass;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

public interface OnlineUserService {
    Set<Object> getOnlineUsers(UserClass cla);
    List<Object> getConsultants(Set<Object> result);


}
