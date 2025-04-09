package komeiji.back.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class MD5Utils {
    public static String toMD5(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(input.getBytes());
        String md5 = HexFormat.of().formatHex(md.digest());

        return md5;
    }
}
