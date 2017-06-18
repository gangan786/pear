package com.example.pear.gson;


/**
 * Created by 陈淦 on 2017/6/3. 用户类
 */

public class User {


    /**
     * code : 20000
     * response : {"id":"2157","phone":"15875064665","username":"何志伟","head_path":"Public/user/2157/user_head/2157-58115a24b18b5.jpg","sex":"m"}
     */

    private int code;
    private ResponseBean response;
    /**
     * error_code : 40000
     * msg : 账号不存在或密码错误
     */

    private int error_code;
    private String msg;


    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ResponseBean getResponse() {
        return response;
    }

    public void setResponse(ResponseBean response) {
        this.response = response;
    }

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

    public static class ResponseBean {
        /**
         * id : 2157
         * phone : 15875064665
         * username : 何志伟
         * head_path : Public/user/2157/user_head/2157-58115a24b18b5.jpg
         * sex : m
         */

        private String id;
        private String phone;
        private String username;
        private String head_path;
        private String sex;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getHead_path() {
            return head_path;
        }

        public void setHead_path(String head_path) {
            this.head_path = head_path;
        }

        public String getSex() {
            return sex;
        }

        public void setSex(String sex) {
            this.sex = sex;
        }
    }
}
