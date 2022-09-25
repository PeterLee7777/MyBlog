package com.lwy.blog.entity.vo;

import com.lwy.blog.entity.pojo.SysUser;
import lombok.Data;

@Data
public class RegisterVo extends SysUser {

    private String account;

    private String password;

    private String nickname;
}
