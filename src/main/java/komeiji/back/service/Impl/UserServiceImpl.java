package komeiji.back.service.Impl;

import komeiji.back.entity.UserClass;
import komeiji.back.service.UserService;
import komeiji.back.repository.UserDao;
import komeiji.back.entity.User;
import komeiji.back.utils.MD5Utils;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;


    @Override
    public Boolean userNameIsLegal(String username) {
       String USERNAME_PATTERN ="^[a-zA-Z][a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    @Override
    public User loginService(String userName, String password) throws NoSuchAlgorithmException {
        System.out.println("userName: " + userName);
        System.out.println("password: " + password);
        User user = userDao.findByUserNameAndPassword(userName, MD5Utils.toMD5(password));
        return user;

    }

    @Override
    public Boolean registerService(User user) throws NoSuchAlgorithmException {
        if(userDao.findByUserName(user.getUserName()) != null){
            //用户名重复
            return false;
        }
        else {
            user.setPassword(MD5Utils.toMD5(user.getPassword()));
            if(user.getNickName().length() == 0 ){
                user.setNickName("User:"+ user.getUserName());
            }
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
    public User getUserById(long id) {
        return userDao.findById(id);
    }

    @Override
    public List<User> getAllUsers() { return userDao.findAll(); }

    @Override
    public int updateUser(User user) {
        return userDao.updateUser(user.getUserClass(), user.getEmail(), user.getId());
    }

    @Override
    public int updatePassword(User user, String password) throws NoSuchAlgorithmException {
        return userDao.updatePassword(MD5Utils.toMD5(password), user.getId());
    }
}
