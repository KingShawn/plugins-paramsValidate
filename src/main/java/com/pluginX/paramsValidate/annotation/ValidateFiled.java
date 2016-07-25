package  com.pluginX.paramsValidate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * 〈参数注解〉<br> 
 *
 * @author Shawn Wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidateFiled {
    /** 
     * 参数索引位置   
     */  
    public int index() default -1 ;  
      
    /** 
     * 如果参数是基本数据类型或String ，就不用指定该参数，如果参数是对象，要验证对象里面某个属性，就用该参数指定属性名 
     */  
    public String filedName() default "" ;  
      
    /** 
     * 正则验证 
     */  
    public String regStr() default "";  
      
    /** 
     * 是否能为空  ， 为true表示不能为空 ， false表示能够为空 
     */  
    public boolean notNull() default true;  
    
    
    /** 
     * 是否能为0 ， 为true表示不能为空 ， false表示能够为空 
     */  
    public boolean notZero() default false;  
    
      
    /** 
     * 是否能为空  ， 为true表示不能为空 ， false表示能够为空 
     */  
    public int maxLen() default -1 ;  
      
    /** 
     * 最小长度 ， 用户验证字符串 
     */  
    public int minLen() default -1 ;  
      
    /** 
     *最大值 ，用于验证数值类型数据 
     */  
    public int maxVal() default -1 ;  
      
    /** 
     *最小值 ，用于验证数值类型数据 
     */  
    public int minVal() default -1 ;  
      
}
