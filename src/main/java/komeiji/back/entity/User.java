package komeiji.back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Table(name = "user")
@Entity
@Schema(name = "User", description = "用户实体类")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "用户ID",example = "1")
    private long id;

    @Schema(description = "用户名",example = "admin",required = true)
    @Column(unique = true,nullable = false)
    private String userName;

    @Schema(description = "密码",example = "123456",required = true)
    @Column(nullable = false)
    private String password;

    @Schema(description = "用户类别",example = "0")
    private UserClass userClass = UserClass.Normal;

    @Schema(description = "邮箱",example = "admin@example.com")
    private String email = "";

    @Schema(description = "用户昵称")
    private String nickName = ""; //用户昵称

    private String qualification = "";

    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", userClass=" + userClass +
                ", email='" + email + '\'' +
                '}';
    }
}
