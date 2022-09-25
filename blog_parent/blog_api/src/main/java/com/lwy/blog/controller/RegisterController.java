package com.lwy.blog.controller;

import com.lwy.blog.entity.vo.RegisterVo;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("register")
public class RegisterController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public Result register(@RequestBody RegisterVo registerVo){
        return loginService.register(registerVo);
    }
}
