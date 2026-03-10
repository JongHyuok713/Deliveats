package com.example.deliveats.exception.store;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.StoreErrorCode;

public class StoreNotFoundException extends CustomException {

    public StoreNotFoundException() {
        super(StoreErrorCode.STORE_NOT_FOUND);
    }
}
