package com.lwy.blog.controller;

import com.lwy.blog.entity.param.LoginParam;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result login(@RequestBody LoginParam loginParam){
        //登录 验证  访问用户表，但是
        return loginService.login(loginParam);
    }

}
