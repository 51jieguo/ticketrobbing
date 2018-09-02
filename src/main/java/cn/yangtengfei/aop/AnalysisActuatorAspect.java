package cn.yangtengfei.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Aspect
@Component
@Slf4j
public class AnalysisActuatorAspect {

    ThreadLocal<Long> beginTime = new ThreadLocal<>();

    @Pointcut("@annotation(analysisActuator)")
    public void serviceStatistics(AnalysisActuator analysisActuator) {
    }

    @Before("serviceStatistics(analysisActuator)")
    public void doBefore(JoinPoint joinPoint, AnalysisActuator analysisActuator) {
        // 记录请求到达时间
        beginTime.set(System.currentTimeMillis());
        log.info("cy666 note:{}", analysisActuator.note());
    }

    @After("serviceStatistics(analysisActuator)")
    public void doAfter(AnalysisActuator analysisActuator) {
        log.info("cy666 statistic time:{}, note:{}", System.currentTimeMillis() - beginTime.get(), analysisActuator.note());
    }

}