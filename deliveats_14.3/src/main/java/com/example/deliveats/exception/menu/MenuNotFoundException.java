package com.example.deliveats.exception.menu;

import com.example.deliveats.exception.CustomException;
import com.example.deliveats.exception.MenuErrorCode;

public class MenuNotFoundException extends CustomException {

    public MenuNotFoundException() {
        super(MenuErrorCode.MENU_NOT_FOUND);
    }
}
