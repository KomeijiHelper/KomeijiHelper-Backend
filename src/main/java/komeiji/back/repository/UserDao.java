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
    User findByQualification(String qualification);

    @Modifying
    @Transactional
    @Query("update User u set u.userClass = ?1,u.email = ?2 where u.id = ?3")
    int updateUser( @Param("1") UserClass userClass, @Param("2") String email, @Param("3") long id);

    @Modifying
    @Transactional
    @Query("update User u set u.password = ?1 where u.id = ?2")
    int updatePassword(@Param("1")  String password, @Param("2") long id);
}
