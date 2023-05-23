package com.wang.reggir.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.wang.reggir.common.BaseContext;
import com.wang.reggir.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
//配置过滤器，看是否处于登录状态
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {

    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        String requestURI = request.getRequestURI();
        log.info("拦截到请求：{}",requestURI);
        String[] urls = {
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/login",
                "/user/sendMsg"
        };

        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求不需要处理");
            filterChain.doFilter(request,response);
            return;
        }
        if( request.getSession().getAttribute("employee") != null){
            log.info("用户{}已登录",request.getSession().getAttribute("employee"));
            //将id入ThreadLocal
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }
        if( request.getSession().getAttribute("user") != null){
            log.info("用户{}已登录",request.getSession().getAttribute("user"));
            //将id入ThreadLocal
            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录，请登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;


//        log.info("拦截到请求：{}",request.getRequestURI());
//        filterChain.doFilter(request,response);
    }

    public boolean check(String[] URIs, String URI){
        for (String uri : URIs) {
            boolean match = PATH_MATCHER.match(uri, URI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
