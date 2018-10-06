package com.owen.service;

/**
 * Created by Administrator on 2018/7/9.
 */
public class ServiceResult<T> {
    private boolean success;
    private String message;
    private T result;

    public ServiceResult(boolean success) {
        this.success = success;
    }

    public ServiceResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ServiceResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }
    public void setResult(T result) {
        this.result = result;
    }

    public static <T>  ServiceResult<T> success(){
        return  new ServiceResult<T>(true);
    }

    public static <T> ServiceResult<T> of(T result){
        ServiceResult<T> serviceResult = new ServiceResult<T>(true);
        serviceResult.setResult(result);
        return  serviceResult;
    }

    public static <T> ServiceResult<T> notFound() {
        return new ServiceResult<T>(false, Message.NOT_FOUND.getValue());
    }

    @Override
    public String toString() {
        return "ServiceResult{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", result=" + result +
                '}';
    }
    public enum Message {
        NOT_FOUND("Not Found Resource!"),
        NOT_LOGIN("User not login!");

        private String value;

        Message(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
