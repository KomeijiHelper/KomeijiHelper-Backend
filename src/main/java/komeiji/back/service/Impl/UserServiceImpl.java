package komeiji.back.service.Impl;

import komeiji.back.entity.UserClass;
import komeiji.back.service.UserService;
import komeiji.back.repository.UserDao;
import komeiji.back.entity.User;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public User loginService(String userName, String password) {
        System.out.println("userName: " + userName);
        System.out.println("password: " + password);
        User user = userDao.findByUserNameAndPassword(userName, password);
        return user;

    }

    @Override
    public Boolean registerService(User user) {
        if(userDao.findByUserName(user.getUserName()) != null){
            //用户名重复
            return false;
        }
        else {
            userDao.save(user);
            return true;
        }
    }

    @Override
    public User getUserByName(String userName) {
        return userDao.findByUserName(userName);
    }

    @Override
    public List<User> getUsersByUserClass(UserClass userClass) {
        return userDao.findAllByUserClass(userClass);
    }

    @Override
    public List<User> getAllUsers() { return userDao.findAll(); }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user.getUserName(), user.getPassword(), user.getUserClass(), user.getEmail(), user.getId());
    }
}
