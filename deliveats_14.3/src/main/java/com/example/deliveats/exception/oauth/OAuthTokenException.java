package com.example.deliveats.exception.oauth;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.OAuthErrorCode;

public class OAuthTokenException extends CustomException {

    public OAuthTokenException() {
        super(OAuthErrorCode.OAUTH_TOKEN_ERROR);
    }
}
