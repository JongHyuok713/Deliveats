package com.example.deliveats.exception.user;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.UserErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(UserErrorCode.USER_NOT_FOUND);
    }
}
