package komeiji.back.service;

import lombok.Getter;

@Getter
public enum EmailCodeStatus {
    // 成功相关状态码
    SUCCESS(100, "发送成功"),

    // 发送限制相关状态码
    RATE_LIMIT_EXCEEDED(200, "发送频率太快，1分钟内只能发送一次"),
    SEND_LIMIT_REACHED(201, "达到发送限制，请稍后再试"),
    SEND_BLOCKED(202, "发送请求已被禁止（邮箱被封禁）"),

    // 验证码有效性相关状态码
    CODE_VERIFIED(300, "验证码认证成功"),
    CODE_EXPIRED(301, "验证码已失效或已过期"),
    CODE_EXISTS(302, "验证码已存在，仍然有效"),
    CODE_ERROR(303, "验证码错误"),

    // 参数和数据问题相关状态码
    INVALID_EMAIL_FORMAT(400, "邮箱格式不正确"),
    EMAIL_ALREADY_REGISTERED(401, "该邮箱已被注册"),
    EMAIL_NOT_FOUND(402, "邮箱不存在"),

    // 系统错误相关状态码
    SYSTEM_ERROR(500, "系统错误，请稍后再试"),
    EMAIL_SEND_FAILED(501, "邮件发送失败"),
    REDIS_OPERATION_FAILED(502, "Redis 操作失败"),
    VERIFICATION_CODE_GENERATION_FAILED(503, "验证码生成失败"),

    // 其他状态码
    USER_VERIFICATION_REQUIRED(600, "系统需要进行用户验证（如验证码）"),
    USER_NOT_LOGGED_IN(601, "用户未登录"),
    USER_REQUEST_TOO_FREQUENT(602, "用户请求过于频繁，系统暂时无法处理");

    private final int code;
    private final String message;

    EmailCodeStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static EmailCodeStatus fromCode(int code) {
        for (EmailCodeStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        return SYSTEM_ERROR;
    }
}
