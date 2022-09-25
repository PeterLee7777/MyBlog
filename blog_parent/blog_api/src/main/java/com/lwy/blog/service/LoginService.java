package com.lwy.blog.service;

import com.lwy.blog.entity.pojo.SysUser;
import com.lwy.blog.entity.param.LoginParam;
import com.lwy.blog.entity.vo.RegisterVo;
import com.lwy.blog.entity.vo.Result;

public interface LoginService {

    /**
     * 用户登录
     * @param loginParam
     * @return
     */
    Result login(LoginParam loginParam);

    SysUser checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册
     * @param registerVo
     * @return
     */
    Result register(RegisterVo registerVo);
}
