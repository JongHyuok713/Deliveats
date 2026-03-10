package com.example.deliveats.exception.delivery;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.DeliveryErrorCode;

public class DeliveryNotFoundException extends CustomException {

    public DeliveryNotFoundException() {
        super(DeliveryErrorCode.DELIVERY_NOT_FOUND);
    }
}
