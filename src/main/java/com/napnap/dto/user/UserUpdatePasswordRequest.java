package com.napnap.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserUpdatePasswordRequest implements Serializable {

    /**
     * 用户密码
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}
