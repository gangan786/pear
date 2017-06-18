package com.example.pear.gson;

/**
 * Created by 陈淦 on 2017/6/11.
 */

public class CodeResponse {

    /**
     * error_code : 40000
     * msg : no login
     */

    private int error_code;
    private String msg;
    /**
     * code : 20000
     * response : Public/user/2157/user_head/2157-59427c242091c.jpg
     */

    private int code;
    private String response;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
