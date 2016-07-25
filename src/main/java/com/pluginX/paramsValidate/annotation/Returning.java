package  com.pluginX.paramsValidate.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 
 * 〈返回类型注解〉<br> 
 * 〈功能详细描述〉
 *
 * @author Shawn Wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Returning {
    @SuppressWarnings("rawtypes")
    Class type() default String.class;  
    String value() default "";
    
}
