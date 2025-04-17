package komeiji.back.service;

import lombok.Getter;

@Getter
public enum EmailCaptchaType {
    REGISTER("register","注册账号"),
    RESET_PASSWORD("resetPwd","重置密码");


    private final String value;
    private final String description;

    EmailCaptchaType(String value,String description) {
        this.value = value;
        this.description = description;
    }

    public static EmailCaptchaType fromValue(String value) {
        for (EmailCaptchaType type : EmailCaptchaType.values()) {
            if(type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No EmailCaptchaType with value " + value);
    }
}
