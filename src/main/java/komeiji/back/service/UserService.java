package komeiji.back.service;

import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface UserService {
    User loginService(String userName ,String password) throws NoSuchAlgorithmException;
    Boolean registerService(User user) throws NoSuchAlgorithmException;
    User getUserByName(String uname);
    List<User> getUsersByUserClass(UserClass userClass);
    List<User> getAllUsers();
    int updateUser(User user);
    int updatePassword(User user, String password) throws NoSuchAlgorithmException;
    User getUserById(long id);
    Boolean userNameIsLegal(String username);
}
