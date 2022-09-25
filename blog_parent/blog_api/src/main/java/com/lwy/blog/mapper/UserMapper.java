package com.lwy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwy.blog.entity.pojo.SysUser;

/**
 * @author 李文阳
 */
public interface UserMapper extends BaseMapper<SysUser> {

    /**
     * 通过id查询用户
     * @param id 用户id
     * @return 用户
     */
    SysUser findUserById01(Long id);

    /**
     * 通过账号查询用户
     * @param account 账号
     * @return 用户
     */
    SysUser selectOneByAccount(String account);
}
