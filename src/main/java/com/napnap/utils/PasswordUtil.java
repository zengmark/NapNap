package com.napnap.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.SecureUtil;

public class PasswordUtil {

    /**
     * 用户密码加密
     *
     * @param password
     * @return
     */
    public static String encryptPassword(String password) {
        // 使用 SHA-256 算法进行加密
        String encryptPassword = SecureUtil.sha256(password);
        // 使用 Base64 编码
        return Base64.encode(encryptPassword);
    }
}
