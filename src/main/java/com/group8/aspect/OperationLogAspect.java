package com.group8.aspect;


import com.group8.entity.CustomUserDetails;
import com.group8.entity.Result;
import com.group8.util.LogMaskUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class OperationLogAspect {

    //only record Controller
    @Around("execution(public * com.group8.controller..*(..))")
    public Object aroundController(ProceedingJoinPoint joinPoint) throws Throwable {

        // record the start time of the method execution
        long startTime = System.currentTimeMillis();

        // execute the method and get the result
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String handler = signature.getDeclaringType().getSimpleName() + "." + signature.getName();

        //
        HttpServletRequest request = getRequest();
        String method = request!=null?request.getMethod():"-";
        String uri = request != null ? request.getRequestURI():"-";
        String ip = request!=null?request.getRemoteAddr():"-";
        String user = currentUser();

        String args = LogMaskUtil.maskArgs(joinPoint.getArgs());

        log.info("API_START user={} ip={} method={} uri={} handler={} args={}",
                user, ip, method, uri, handler, args);

        try {
            Object result = joinPoint.proceed();
            long cost = System.currentTimeMillis() - startTime;

            if (result instanceof Result r) {
                log.info("API_END user={} method={} uri={} handler={} code={} msg={} cost={}ms",
                        user, method, uri, handler, r.getCode(), r.getMsg(), cost);
            } else {
                log.info("API_END user={} method={} uri={} handler={} cost={}ms result={}",
                        user, method, uri, handler, cost, LogMaskUtil.maskObject(result));
            }
            return result;
        }catch (Exception e){
            long cost = System.currentTimeMillis() - startTime;
            log.error("API_ERROR user={} method={} uri={} handler={} cost={}ms exType={} exMsg={}",
                    user, method, uri, handler, cost, e.getClass().getSimpleName(), e.getMessage());
            throw e;
        }

    }
    private HttpServletRequest getRequest() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attrs == null ? null : attrs. getRequest();
    }

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null||authentication.getPrincipal()==null){
            return"anonymous";
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails cud) {
            return cud.getUsername() + "(" + cud.getId() + "))";
        }
        return String.valueOf(principal);


    }
}
