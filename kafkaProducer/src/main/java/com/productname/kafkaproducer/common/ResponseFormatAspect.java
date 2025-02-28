package com.productname.kafkaproducer.common;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ResponseFormatAspect {

    @Around("execution(* com.productname.kafkaproducer.controller..*(..))")
    public Object formatResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            Object result = joinPoint.proceed();
            String message = "Operation successful";
            return new CommonResponseModel<>(HttpStatus.OK, message, result);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            return new CommonResponseModel<>(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
        }
    }

}
