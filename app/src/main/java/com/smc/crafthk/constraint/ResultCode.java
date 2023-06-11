package com.smc.crafthk.constraint;

import javax.xml.transform.Result;

public enum ResultCode {
    REGISTRATION_SUCCEED(1000),
    USER_PROFILE_IMAGE(1001),
    PICK_SHOP_LOCATION_SUCCEED(1002),
    PICK_SHOP_LOCATION(1003),
    REQUEST_IMAGE_PERMISSION(1004),
    SHOP_IMAGE(1005);

    int code;
    ResultCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
