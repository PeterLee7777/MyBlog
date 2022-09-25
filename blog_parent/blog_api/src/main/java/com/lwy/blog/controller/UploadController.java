package com.lwy.blog.controller;

import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.utils.QiNiuUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {

    @Autowired
    private QiNiuUtils qiNiuUtils;

    @PostMapping
    public Result upload(@RequestParam("image") MultipartFile file){

        //原始文件名称 比如 aa.png
        String originalFilename = file.getOriginalFilename();

        //唯一的文件名称
        String fileName = UUID.randomUUID().toString() + "." + StringUtils.substringAfterLast(originalFilename, ".");

        //上传到哪
        boolean upload = qiNiuUtils.upload(file, fileName);
        if (upload){
            return Result.success(QiNiuUtils.url + fileName);
        }
        return Result.fail(20001,"上传失败");
    }

}
