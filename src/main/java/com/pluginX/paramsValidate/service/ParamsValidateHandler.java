package com.pluginX.paramsValidate.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.pluginX.paramsValidate.annotation.ParamValidate;
import com.pluginX.paramsValidate.annotation.Returning;
import com.pluginX.paramsValidate.annotation.ValidateFiled;
import com.pluginX.paramsValidate.exception.TypeMappingException;
import com.pluginX.paramsValidate.util.StringUtil;

/**
 * 
 * 对使用@ParamValidate 注解的方法进行代理参数校验 <br> 
 *
 * @author Shawn Wang
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
@Service
@Aspect
public class ParamsValidateHandler {
        private Logger LOGGER;  
        private Object target ;
        private Method method ;
        private ParamValidate pv ;
        /**
         *   
         * 功能描述：在进入切面方法时，进行判断 <br>
         * 
         * @ParamValidate(returnType={@ReturnType(type=HashMap.class)},fileds = {  
         * index=0 表示下面方法的第一个参数,不能为空，最大长度为10,最小长度为3 ,匹配此正则表达式
         * @ValidateFiled(index=0 , notNull=true , maxLen = 10 , minLen = 3 ,regStr= "^\\w+@\\w+\\.com$")) ,  
         * @ValidateFiled(index=1 , notNull=true , maxLen = 5 , minLen = 2 ) }) 
         * @param joinPoint
         * @return
         * @throws Throwable
         * @see [相关类/方法](可选)
         * @since [产品/模块版本](可选)
         */
        @SuppressWarnings({ "finally", "rawtypes" })  
        @Around("@annotation(com.pluginX.paramsValidate.annotation.ParamValidate)")  
        public Object validateAround(ProceedingJoinPoint joinPoint) throws Throwable  {  
            boolean flag = false ;      
            Object[] args =  null ;  
            String methodName = null;  
            try{  
                methodName = joinPoint.getSignature().getName();  
                target = joinPoint.getTarget(); 
                LOGGER = LoggerFactory.getLogger(target.getClass());
                //得到拦截的方法      
                method = getMethodByClassAndName(target.getClass(), methodName);   
                //方法的参数               
                args = joinPoint.getArgs();  
                
                //目标方法的ParamValidate注解
                pv = (ParamValidate)getAnnotationByMethod(method ,ParamValidate.class);      
                flag = validateFiled(pv.fileds() , args);  
            }catch(Exception e){  
                flag = false;  
            }finally{  
                //验证通过
                if(flag){  
                    LOGGER.info(target.getClass() + "调用"+ method.getName()+" validate passed..."); 
                    return joinPoint.proceed();  
                }else{
                    //验证不通过
                    Class methodType = method.getReturnType();   //得到方法返回值类型  
                    StringBuilder str = new StringBuilder("");                   
                    if(args != null){
                        for(Object obj: args){
                            String arg ="";
                            if(obj == null){
                                arg = "null";
                            }else{
                                arg = obj.toString();
                            }
                            str.append("["+ arg+ "]");
                        }
                    }else{
                        str.append("[null]");
                    }                
                    //打印错误日志
                    LOGGER.error(target.getClass() +"调用"+method.getName()+"时存在参数不合法,参数分别为：" +str.toString()); 
                    //返回类型
                    Object obj =  this.getReturnType(methodType);                            
                    return obj;
                   }
              }           
        }  
  
    
      /**
       * 
       * 功能描述: 验证参数是否合法 <br>
       *
       * @param valiedatefiles
       * @param args
       * @return
       * @throws SecurityException
       * @throws IllegalArgumentException
       * @throws NoSuchMethodException
       * @throws IllegalAccessException
       * @throws InvocationTargetException
       * @see [相关类/方法](可选)
       * @since [产品/模块版本](可选)
       */
        private boolean validateFiled( ValidateFiled[] valiedatefiles , Object[] args) throws SecurityException, IllegalArgumentException, NoSuchMethodException, IllegalAccessException, InvocationTargetException{        
           //设置默认值
            if(ArrayUtils.isEmpty(valiedatefiles)){
               if(args != null){
                for(int i = 0 ;i < args.length ;i++){
                    Object arg = args[i];
                    if(StringUtil.isEmpty(arg)) {
                        return false;  
                    }else{
                        //为整型并且为0,则参数有误                    
                        if(arg instanceof Integer && Integer.valueOf(0).equals(arg)){
                            return false;  
                        }
                        if(arg instanceof Long && Long.valueOf(0).equals(arg)){
                            return false;  
                       }
                        if(arg instanceof Double && Double.valueOf(0).equals(arg)){
                            return false;  
                       }
                    }
                  }
                }
            }else{
                //根据用户设置的来进行判断
                for (ValidateFiled validateFiled : valiedatefiles) {  
                Object arg = null;
                if("".equals(validateFiled.filedName()) ){  
                    arg = args[validateFiled.index()];  
                }else{  
                    arg = getFieldByObjectAndFileName(args[validateFiled.index()] ,  
                            validateFiled.filedName());  
                }    
                if(validateFiled.notNull()){        //判断参数是否为空  
                    if(StringUtil.isEmpty(arg)) {
                        return false;  
                    }
                }else{      //如果该参数能够为空，并且当参数为空时，就不用判断后面的了 ，直接返回true  
                    if(arg == null )  
                        return true;  
                }  
                if(validateFiled.notZero()){ //判断是否为0
                    return false;  
                }
                if(validateFiled.maxLen() > 0){      //判断字符串最大长度  
                    if(((String)arg).length() > validateFiled.maxLen())  
                        return false;  
                }   
                if(validateFiled.minLen() > 0){      //判断字符串最小长度  
                    if(((String)arg).length() < validateFiled.minLen())  
                        return false;  
                }    
                if(validateFiled.maxVal() != -1){   //判断数值最大值  
                    if( (Integer)arg > validateFiled.maxVal())   
                        return false;  
                }    
                if(validateFiled.minVal() != -1){   //判断数值最小值  
                    if((Integer)arg < validateFiled.minVal())  
                        return false;  
                }    
                if(!"".equals(validateFiled.regStr())){ //判断正则  
                    if(arg instanceof String){  
                        if(!((String)arg).matches(validateFiled.regStr()))  
                            return false;  
                    }else{  
                        return false;  
                    }  
                }  
               }  
            }
            return true;  
        }  
      
        /**
         *  
         * 功能描述: 根据对象和属性名得到 属性 <br>
         *
         * @param targetObj
         * @param fileName
         * @return
         * @throws SecurityException
         * @throws NoSuchMethodException
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         * @throws InvocationTargetException
         * @see [相关类/方法](可选)
         * @since [产品/模块版本](可选)
         */
        private Object getFieldByObjectAndFileName(Object targetObj , String fileName) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException{  
            String tmp[] = fileName.split("\\.");  
            Object arg = targetObj ;  
            for (int i = 0; i < tmp.length; i++) {  
                Method methdo = arg.getClass().  
                        getMethod(getGetterNameByFiledName(tmp[i]));  
                arg = methdo.invoke(arg);  
            }  
            return arg ;  
        }  
  
         /**
          * 
          * 功能描述: 根据属性名 得到该属性的getter方法名 <br>
          *
          * @param fieldName
          * @return
          * @see [相关类/方法](可选)
          * @since [产品/模块版本](可选)
          */
        private String getGetterNameByFiledName(String fieldName){  
            return "get" + fieldName.substring(0 ,1).toUpperCase() + fieldName.substring(1) ;  
        }  
      
        /**
         *  
         * 功能描述:根据目标方法和注解类型  得到该目标方法的指定注解  <br>
         * 〈功能详细描述〉
         *
         * @param method
         * @param annoClass
         * @return
         * @see [相关类/方法](可选)
         * @since [产品/模块版本](可选)
         */
        @SuppressWarnings("rawtypes")
        private Annotation getAnnotationByMethod(Method method , Class annoClass){  
            Annotation all[] = method.getAnnotations();  
            for (Annotation annotation : all) {  
                if (annotation.annotationType() == annoClass) {  
                    return annotation;  
                }  
            }  
            return null;  
        }  
      
        /**
         *  
         * 功能描述:根据类和方法名得到方法  <br>
         *
         * @param c
         * @param methodName
         * @return
         * @see [相关类/方法](可选)
         * @since [产品/模块版本](可选)
         */
        @SuppressWarnings("rawtypes")
        private Method getMethodByClassAndName(Class c , String methodName){  
            Method[] methods = c.getDeclaredMethods();  
            for (Method method : methods) {  
                if(method.getName().equals(methodName)){  
                    return method ;  
                }  
            }  
            return null;  
        }  
        /**
         * 
         * 功能描述:获取返回类型 <br>
         *
         * @param methodType
         * @return
         * @throws InstantiationException
         * @throws IllegalAccessException
         * @see [相关类/方法](可选)
         * @since [产品/模块版本](可选)
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private Object getReturnType(Class methodType) throws InstantiationException, IllegalAccessException{
               Object obj = null ; 
               //返回类型
           Returning[] type  = pv.returning(); 
           if(ArrayUtils.isNotEmpty(type)){
               for(Returning re : type ){                      
                Class clazz = re.type();                        
                   if(clazz.equals(methodType) || methodType.isAssignableFrom(clazz)){  
                     String value = re.value();
                       if(!StringUtil.isEmpty(value)){
                         if(clazz == String.class){
                             obj = value;
                         }else if(clazz == Integer.class ){
                             obj = Integer.valueOf(value);
                         }else if(clazz == Boolean.class){
                             obj = Boolean.valueOf(value);
                         }else if(clazz == Long.class){
                             obj = Long.valueOf(value);
                         }else if(clazz == Float.class){
                             obj = Float.valueOf(value);
                         }else if(clazz == Double.class){
                             obj = Double.valueOf(value);
                         }else if(clazz == int.class ){
                             obj = Integer.parseInt(value);
                         }else if(clazz == boolean.class){
                             obj = Boolean.parseBoolean(value);
                         }else if(clazz == long.class){
                             obj = Long.parseLong(value);
                         }else if(clazz == float.class){
                             obj = Float.parseFloat(value);
                         }else if(clazz == double.class){
                             obj = Double.parseDouble(value);
                       }   
                     }else{
                         obj =  clazz.newInstance();
                       } 
                     } else{     
                       //打印错误日志
                       LOGGER.error("@ParamValidate:" + target.getClass() + "调用"+ method.getName()+"时存在实际返回类型和自定义返回类型不匹配,实际返回类型为：" +methodType); 
                           throw new TypeMappingException();
                       }
                   }
               }
               return obj;
           }
        
}
