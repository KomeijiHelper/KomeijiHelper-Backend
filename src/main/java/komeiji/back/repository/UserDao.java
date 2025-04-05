package komeiji.back.repository;

import jakarta.transaction.Transactional;
import komeiji.back.entity.UserClass;
import org.springframework.data.jpa.repository.JpaRepository;
import komeiji.back.entity.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.*;

import java.util.List;

@Repository
public interface UserDao extends JpaRepository<User,Long>{

    User findById(long id);
    User findByUserName(String uname);
    User findByIdAndPassword(long id, String password);
    User findByUserNameAndPassword(String uname, String password);
    List<User> findAllByUserClass(UserClass userClass);
    List<User> findAll();

    @Modifying
    @Transactional
    @Query("update User u set u.password = ?2,u.userClass = ?3,u.email = ?4 where u.id = ?5")
    int updateUser(@Param("1") String uname, @Param("2") String password, @Param("3") UserClass userClass, @Param("4") String email, @Param("5") long id);
}
