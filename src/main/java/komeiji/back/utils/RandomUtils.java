package komeiji.back.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class RandomUtils {
    private static final Logger logger = LoggerFactory.getLogger(RandomUtils.class);
    private static final String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public static String randomCode(String algorithm,int length) {
        SecureRandom randomBuilder;
        try {
            randomBuilder = SecureRandom.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            logger.warn(e.getMessage());
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(randomBuilder.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
