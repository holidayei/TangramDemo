package com.holiday.tangram.bean;

public class ResultBean<T> {
    private Integer errorCode;//错误码，成功为0
    private String errorMsg;//解释信息
    private T data;//数据集

    public Integer getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(Integer errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
