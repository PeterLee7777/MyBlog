package com.lwy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwy.blog.entity.pojo.SysUser;

public interface UserMapper extends BaseMapper<SysUser> {

    SysUser findUserById01(Long id);

    SysUser selectOneByAccount(String account);
}
