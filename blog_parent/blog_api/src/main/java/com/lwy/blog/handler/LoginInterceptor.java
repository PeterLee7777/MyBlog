package com.lwy.blog.handler;

import com.alibaba.fastjson.JSON;
import com.lwy.blog.entity.pojo.SysUser;
import com.lwy.blog.entity.vo.ErrorCode;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.LoginService;
import com.lwy.blog.utils.UserThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private LoginService loginService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //在执行controller方法（handler）之前进行执行
        /**
         * 1 需要判断 请求的接口路径 是否为HandlerMethod（Controller方法）
         * 2 判断token是否为空，如果为空，未登录
         * 3 如果token不为空登录验证 loginService checkToken
         * 4 如果认证成功  放行即可
         */
        if(!(handler instanceof HandlerMethod)){
            //handler  可能是RequestResourceHandler  springboot程序 访问静态资源  默认去classpath下的static目录去查询
            return true;
        }
        String token = request.getHeader("Authorization");

        log.info("=================request start===========================");
        String requestURI = request.getRequestURI();
        log.info("request uri:{}",requestURI);
        log.info("request method:{}",request.getMethod());
        log.info("token:{}", token);
        log.info("=================request end===========================");

        if(StringUtils.isBlank(token)){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
        SysUser sysUser = loginService.checkToken(token);

        if(sysUser == null){
            Result result = Result.fail(ErrorCode.NO_LOGIN.getCode(), ErrorCode.NO_LOGIN.getMsg());
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(JSON.toJSONString(result));
            return false;
        }
        //登录验证成功，放行
        //我希望在controller中 直接获取用户的信息 怎么获取?
        UserThreadLocal.put(sysUser);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserThreadLocal.remove();
    }
}
