package com.example.deliveats.exception.oauth;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.OAuthErrorCode;

public class OAuthUserInfoException extends CustomException {

    public OAuthUserInfoException() {
        super(OAuthErrorCode.OAUTH_USERINFO_ERROR);
    }
}
