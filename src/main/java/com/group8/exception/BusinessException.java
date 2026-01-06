package com.group8.exception;

/**
 * 自定义业务异常类
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    /**
     * 错误码
     */
    private Integer code;
    
    /**
     * 构造方法
     */
    public BusinessException() {
        super();
    }
    
    /**
     * 构造方法
     * @param message 错误信息
     */
    public BusinessException(String message) {
        super(message);
        this.code = 0;
    }
    
    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误信息
     */
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    /**
     * 构造方法
     * @param message 错误信息
     * @param cause 异常原因
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.code = 0;
    }
    
    /**
     * 构造方法
     * @param code 错误码
     * @param message 错误信息
     * @param cause 异常原因
     */
    public BusinessException(Integer code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
    
    /**
     * 获取错误码
     * @return 错误码
     */
    public Integer getCode() {
        return code;
    }
    
    /**
     * 设置错误码
     * @param code 错误码
     */
    public void setCode(Integer code) {
        this.code = code;
    }
}