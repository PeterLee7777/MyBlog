package com.lwy.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.lwy.blog.entity.pojo.SysUser;
import com.lwy.blog.entity.vo.ErrorCode;
import com.lwy.blog.entity.param.LoginParam;
import com.lwy.blog.entity.vo.RegisterVo;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.LoginService;
import com.lwy.blog.service.UserService;
import com.lwy.blog.utils.JWTUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private static final String salt = "lwy!@#";

    @Override
    public Result login(LoginParam loginParam) {
        /**
         * 1 检查参数是否合法
         * 2 根据用户名密码去User表中查询 是否存在
         * 3 如果不存在 登陆失败
         * 4 如果存在， 使用JWT生成token 返回给前端
         * 5 token放入redis当中，redis token：user信息  设置过期时间
         *          （登录认证的时候，先认证token字符串是否合法，再去redis认证是否存在
         */
        String account = loginParam.getAccount();
        String password = loginParam.getPassword();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        password = DigestUtils.md5Hex(password + salt);
        SysUser sysUser = userService.findUser(account,password);
        if(sysUser == null){
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(),ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        String token = JWTUtils.createToken(sysUser.getId());

        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    @Override
    public SysUser checkToken(String token) {
        if(StringUtils.isBlank(token)){
            return null;
        }
        Map<String, Object> map = JWTUtils.checkToken(token);
        if(map == null){
            return null;
        }
        String userJson = redisTemplate.opsForValue().get("TOKEN_" + token);
        if(StringUtils.isBlank(userJson)){
            return null;
        }
        SysUser user = JSON.parseObject(userJson, SysUser.class);
        return user;
    }

    @Override
    public Result logout(String token) {
        redisTemplate.delete("TOKEN_" + token);
        return Result.success(null);
    }

    @Transactional
    @Override
    public Result register(RegisterVo registerVo) {
        /**
         * 验证注册信息
         *      为空则返回错误
         *      不为空则查询数据库用户名、账户是否存在
         *          存在则返回错误
         *          不存在  插入用户信息到数据库
         */
        String account = registerVo.getAccount();
        String password = registerVo.getPassword();
        String nickname = registerVo.getNickname();
        if(StringUtils.isBlank(account) || StringUtils.isBlank(password) || StringUtils.isBlank(nickname)){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        SysUser user = userService.findUserByAccount(account);
        if(user != null){
            return Result.fail(ErrorCode.USERNAME_ALREADY_EXISTS.getCode(),ErrorCode.USERNAME_ALREADY_EXISTS.getMsg());
        }
        SysUser sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+salt));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setIsAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        this.userService.save(sysUser);

        String token = JWTUtils.createToken(sysUser.getId());
        redisTemplate.opsForValue().set("TOKEN_" + token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);

        return Result.success(token);
    }

    @Test
    public void test(){
        String password = DigestUtils.md5Hex("admin" + salt);
        System.out.println("password = " + password);
    }
}
