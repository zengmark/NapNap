package com.napnap.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRegisterRequest implements Serializable {

    private String userName;

    private String userAccount;

    private String userPassword;

    private static final long serialVersionUID = 1L;
}
