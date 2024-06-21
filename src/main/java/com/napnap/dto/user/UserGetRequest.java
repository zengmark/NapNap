package com.napnap.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserGetRequest implements Serializable {

    private Long userId;

    private static final long serialVersionUID = 1L;
}
