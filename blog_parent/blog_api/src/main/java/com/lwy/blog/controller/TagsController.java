package com.lwy.blog.controller;

import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("tags")
public class TagsController {

    @Autowired
    private TagService tagService;

    @GetMapping("hot")
    public Result hotTags(){
        int limit = 6;
        return tagService.hotTags(limit);
    }

    @GetMapping
    public Result findAll(){
        return tagService.findAll();
    }

    @GetMapping("detail")
    public Result findAllDetail(){
        return tagService.findAllDetail();
    }

    @GetMapping("detail/{id}")
    public Result findAllDetail(@PathVariable Long id){
        return tagService.findAllDetailById(id);
    }
}
