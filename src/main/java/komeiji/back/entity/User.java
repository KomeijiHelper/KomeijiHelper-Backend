package komeiji.back.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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


    @Schema(description = "用户名",example = "admin")
    @NotNull(message = "用户名不能为空")
    @Column(unique = true,nullable = false)
    private String userName;

    @Schema(description = "密码",example = "123456")
    @NotNull(message = "密码不能为空")
    @Column(nullable = false)
    private String password;

    @Schema(description = "用户类别",example = "0")
    private UserClass userClass = UserClass.Normal;

    @Schema(description = "邮箱",example = "admin@example.com")
    @Column(unique = true)
    private String email = "";

    @Schema(description = "用户昵称")
    private String nickName = ""; //用户昵称

    private String qualification = "";

    private String emergencyContact = "";

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
