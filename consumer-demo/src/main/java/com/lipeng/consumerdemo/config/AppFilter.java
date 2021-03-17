package com.lipeng.consumerdemo.config;

import com.lipeng.common.utils.IpTraceUtils;
import com.lipeng.common.utils.WebUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author: lipeng 910138
 * @Date: 2019/8/27 10:45
 */
@Slf4j
@WebFilter(filterName = "appFilter", urlPatterns = "/")
@Component
public class AppFilter implements Filter {

    public static ThreadLocal<String> ip = new ThreadLocal<>();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            IpTraceUtils.setIp(WebUtils.getIpAddr(req));
            HttpServletResponse resp = (HttpServletResponse) response;
            resp.setHeader("Access-Control-Allow-Origin", "*");
            resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            resp.setHeader("Access-Control-Max-Age", "3600");
            resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");
            chain.doFilter(req, resp);
        } finally {
            IpTraceUtils.clear();
        }
    }

    @Override
    public void destroy() {
    }

}