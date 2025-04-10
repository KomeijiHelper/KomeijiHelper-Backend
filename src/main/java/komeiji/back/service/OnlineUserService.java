package komeiji.back.service;

import komeiji.back.entity.UserClass;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public interface OnlineUserService {
    Set<Object> getOnlineUsers(UserClass cla);


}
