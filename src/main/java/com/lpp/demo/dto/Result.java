package com.lpp.demo.dto;

import lombok.Data;

@Data
public class Result<T> {
    //返回信息
    private String message;

    private int code;

    //数据是否正常请求
    private boolean success;
    //具体返回的数据
    private T detail;

    public Result(boolean success, int code, String message, T detail) {
        this.message = message;
        this.code = code;
        this.success = success;
        this.detail = detail;
    }

    public static Result success() {
        Result<Object> result = new Result<>(true, 200, "ok", null);
        return result;
    }

    public static Result success(Object data) {
        Result<Object> result = new Result<>(true, 200, "ok", data);
        return result;
    }

    public static Result fail(int code, String msg) {
        Result result = new Result(false, code, msg, null);
        return result;
    }
}


