package com.example.deliveats.exception.order;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.OrderErrorCode;

public class OrderNotFoundException extends CustomException {

    public OrderNotFoundException() {
        super(OrderErrorCode.ORDER_NOT_FOUND);
    }
}
