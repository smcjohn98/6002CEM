package com.smc.crafthk.constraint;

import javax.xml.transform.Result;

public enum ResultCode {
    REGISTRATION_SUCCEED(1000),
    USER_PROFILE_IMAGE(1001);

    int code;
    ResultCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
