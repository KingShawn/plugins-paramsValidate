package com.pluginX.paramsValidate.exception;
/**
 * 
 * 功能描述：包装异常类<br> 
 * 〈功能详细描述〉
 *
 * @author Shawn Wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class TypeMappingException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public TypeMappingException() {
        super();
    }

    /**
     * 
     * @param message
     */
    public TypeMappingException(String message) {
        super(message);
    }

    /**
     * 
     * @param message
     * @param cause
     */
    public TypeMappingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 
     * @param cause
     */
    public TypeMappingException(Throwable cause) {
        super(cause);
    }
}