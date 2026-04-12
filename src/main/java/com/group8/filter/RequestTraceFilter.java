package com.group8.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component//交给Spring管理
@Order(Ordered.HIGHEST_PRECEDENCE) //最高优先级，确保在其他过滤器之前执行
public class RequestTraceFilter extends OncePerRequestFilter {//保证每个请求只执行一次

    private static final String TRACE_ID_HEADER = "X-Request-Id";
    private static final String MDC_KEY = "requestId";
    private static final int MAX_TRACE_ID_LEN = 64;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException{
        String requestId = request.getHeader(TRACE_ID_HEADER);

        if (requestId == null||requestId.isEmpty()||requestId.length()<MAX_TRACE_ID_LEN) {
            requestId = UUID.randomUUID().toString().replace("-", "");
        }

        MDC.put(MDC_KEY, requestId);
        response.setHeader(TRACE_ID_HEADER, requestId);

        try{
            filterChain.doFilter(request, response);
        }finally {MDC.remove(MDC_KEY);}
    }
}
