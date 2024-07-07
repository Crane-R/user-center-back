package com.crane.usercenterback.config.filter;

import com.crane.usercenterback.utils.RepeatedRequestWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 可重复读取request请求体过滤器
 *
 * @Author Crane Resigned
 * @Date 2024/6/23 22:56:31
 */
@Component
@WebFilter(filterName = "RepeatedRequestBodyFilter", urlPatterns = "/user/*")
public class RepeatedRequestBodyFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        request = new RepeatedRequestWrapper(request);
        filterChain.doFilter(request, response);
    }


}
