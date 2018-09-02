package cn.yangtengfei.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
public class UserAccessAspect {

    /*@Pointcut(value = "@annotation(cn.yangtengfei.aop.UserAccess)")
    public void access() {  
  
    }  
  
    @Before("access()")
    public void deBefore(JoinPoint joinPoint) throws Throwable {
        System.out.println("second before");  
    } */
  
    @Around("@annotation(userAccess)")
    public Object around(ProceedingJoinPoint pjp, UserAccess userAccess) {
        //获取注解里的值  
        log.info("second around:" + userAccess.desc());
        try {
            Object o =  pjp.proceed();
            //System.out.println("second around proceed，结果是 :" + o);
            log.info("=============================");
            return o;
        } catch (Throwable throwable) {  
            throwable.printStackTrace();  
            return null;  
        }  
    }  
} 