package com.lwy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lwy.blog.entity.pojo.SysUser;
import com.lwy.blog.entity.vo.*;
import com.lwy.blog.mapper.UserMapper;
import com.lwy.blog.service.LoginService;
import com.lwy.blog.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LoginService loginService;

    @Override
    public SysUser findUserById01(Long id){
        SysUser user = userMapper.findUserById01(id);


        if(user == null){
            SysUser sysUser = new SysUser();
            sysUser.setNickname("李文阳");
        }
        return user;

    }

    @Override
    public SysUser findUser(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account);
        queryWrapper.eq(SysUser::getPassword,password);
        queryWrapper.select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname);
        queryWrapper.last("limit 1");
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public Result findUserByToken(String token) {
        /**
         * 1 token 合法性校验
         *      是否为空，解析是否成功  redis是否存在
         * 2 如果校验失败  返回错误
         * 3 如果成功，返回对用的结果 LoginUserVo
         */

        SysUser user = loginService.checkToken(token);
        if(user == null){
            Result.fail(ErrorCode.TOKEN_ERROR.getCode(),ErrorCode.TOKEN_ERROR.getMsg());
        }
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(user.getId());
        loginUserVo.setAccount(user.getAccount());
        loginUserVo.setAvatar(user.getAvatar());
        loginUserVo.setNickname(user.getNickname());
        return Result.success(loginUserVo);
    }

    @Override
    public SysUser findUserByAccount(String account) {

        SysUser user = userMapper.selectOneByAccount(account);
        return user;
    }

    @Override
    public void save(SysUser sysUser) {
        //注意 mybatis-plus默认生成的id,采用了雪花算法
        userMapper.insert(sysUser);
    }

    @Override
    public UserVo findUserVoById(Long id) {
        SysUser sysUser = userMapper.selectById(id);
        if (sysUser == null){
            sysUser = new SysUser();
            sysUser.setId(1L);
            sysUser.setAvatar("/static/img/logo.b3a48c0.png");
            sysUser.setNickname("码神之路");
        }
        UserVo userVo = new UserVo();
        userVo.setAvatar(sysUser.getAvatar());
        userVo.setNickname(sysUser.getNickname());
        userVo.setId(sysUser.getId());
        return userVo;
    }

//    @Override
//    public SysUser addUser(RegisterVo registerVo) {
//        userMapper.insert(registerVo);
//        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(SysUser::getAccount,registerVo.getAccount());
//        queryWrapper.select(SysUser::getId,SysUser::getAccount);
//        SysUser user = userMapper.selectOne(queryWrapper);
//        return user;
//    }


}
