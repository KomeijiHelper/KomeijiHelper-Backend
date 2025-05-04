package komeiji.back.dto;

import lombok.Data;

@Data
public class CaptchaDTO {
    private String captcha;
    private String type;
}
