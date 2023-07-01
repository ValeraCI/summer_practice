package com.ledivax.aspects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Aspect
public class LoggableAspect {
    private final Logger logger = LoggerFactory.getLogger(LoggableAspect.class);

    @Pointcut(value = "@annotation(com.ledivax.annotations.Loggable)")
    public void loggable() {
    }

    @Around("loggable()")
    public Object afterReturning(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Object result;

        try {
            result = proceedingJoinPoint.proceed();
        } catch (Throwable e) {
            logger.error("Method {} was throwing exception {}:{}", proceedingJoinPoint.getSignature().toLongString(),
                    e.getClass(), e.getMessage());
            throw e;
        }

        if (Objects.isNull(result)) {
            logger.info("Method {} was finished", proceedingJoinPoint.getSignature().toLongString());
        } else {
            logger.info("Method {} was finished and return {}", proceedingJoinPoint.getSignature().toLongString(),
                    result);
        }
        return result;
    }
}
