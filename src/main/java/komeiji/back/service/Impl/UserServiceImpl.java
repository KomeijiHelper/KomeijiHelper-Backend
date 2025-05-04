package komeiji.back.service.Impl;

import jakarta.annotation.Resource;
import komeiji.back.entity.Consultant;
import komeiji.back.entity.User;
import komeiji.back.entity.UserClass;
import komeiji.back.repository.ChatRecordDao;
import komeiji.back.repository.ConsultantDao;
import komeiji.back.repository.UserDao;
import komeiji.back.service.UserService;
import komeiji.back.utils.MD5Utils;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class UserServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Resource
    private ConsultantDao consultantDao;

    @Resource
    private ChatRecordDao chatRecordDao;


    @Override
    public Boolean userNameIsLegal(String username) {
       String USERNAME_PATTERN ="^[a-zA-Z][a-zA-Z0-9_]{5,19}$";
        Pattern pattern = Pattern.compile(USERNAME_PATTERN);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    @Override
    public User loginService(String userName, String password) throws NoSuchAlgorithmException {
        //TODO 用户名或者邮箱登录
        System.out.println("userName: " + userName);
        System.out.println("password: " + password);
        User user = userDao.findByUserNameAndPassword(userName, MD5Utils.toMD5(password));
        if(user != null){
            addConsultantInfo(user);
        }
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
            if(user.getNickName().isEmpty()){
                user.setNickName("User:"+ user.getUserName());
            }
            userDao.save(user);
            User newUser = userDao.findByUserName(user.getUserName());
            addConsultantInfo(newUser);
            return true;
        }
    }

    public void addConsultantInfo(User user){ //NOTICE 用于在consultant表中添加
        if(user.getUserClass() != UserClass.Assistant){
            return;
        }
        else{
            if(consultantDao.findByConsultantName(user.getUserName()) != null){
                return;
            }
            Float avgScore = chatRecordDao.getAverageScore(user.getUserName());
            Integer totalRecord = chatRecordDao.countByConsultantName(user.getUserName());
            Integer scoreRecord = chatRecordDao.countByConsultantNameAndScore(user.getUserName());

            float avg = avgScore!=null ? avgScore : 0.0f;
            int totalR = totalRecord!=null ? totalRecord : 0;
            int scoreR = scoreRecord!=null ? scoreRecord : 0;

            Consultant consultant = new Consultant(user.getId(),user.getUserName(), avg, totalR, scoreR);
            consultantDao.save(consultant);
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
        return userDao.updateUser(user.getUserClass(), user.getEmail(), user.getNickName(), user.getId());
    }

    @Override
    public int updatePassword(User user, String password) throws NoSuchAlgorithmException {
        return userDao.updatePassword(MD5Utils.toMD5(password), user.getId());
    }

    @Override
    public int updateUserInfo(User user) {
        return userDao.updateUserInfo(user.getNickName(), user.getEmail(), user.getQualification(), user.getEmergencyContact(), user.getId());
    }
}
