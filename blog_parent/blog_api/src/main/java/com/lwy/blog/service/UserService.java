package com.lwy.blog.service;

import com.lwy.blog.entity.pojo.SysUser;
import com.lwy.blog.entity.vo.RegisterVo;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.entity.vo.UserVo;

public interface UserService {

    SysUser findUserById01(Long id);

    SysUser findUser(String account, String password);

    /**
     * 根据token查询用户信息
     * @param token
     * @return
     */
    Result findUserByToken(String token);

    /**
     * 根据账号查找用户
     * @param account
     * @return
     */
    SysUser findUserByAccount(String account);

//    SysUser addUser(RegisterVo registerVo);

    /**
     * 保存用户
     * @param sysUser
     */
    void save(SysUser sysUser);

    UserVo findUserVoById(Long authorId);
}
