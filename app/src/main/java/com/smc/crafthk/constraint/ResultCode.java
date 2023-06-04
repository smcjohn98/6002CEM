package com.smc.crafthk.constraint;

import javax.xml.transform.Result;

public enum ResultCode {
    REGISTRATION_SUCCEED(1000);

    int code;
    ResultCode(int code){
        this.code = code;
    }
    public int getCode(){
        return this.code;
    }
}
