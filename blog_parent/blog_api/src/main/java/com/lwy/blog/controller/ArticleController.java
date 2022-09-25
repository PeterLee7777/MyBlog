package com.lwy.blog.controller;

import com.lwy.blog.common.aop.LogAnnotation;
import com.lwy.blog.common.cache.Cache;
import com.lwy.blog.entity.param.ArticleParam;
import com.lwy.blog.entity.param.PageParams;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李文阳
 */
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 首页 文章列表
     * @param pageParams
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章",operation = "获取文章列表")
    @Cache(name = "listArticle")
    public Result listArticle(@RequestBody PageParams pageParams){
        return articleService.listArticle(pageParams);
    }

    /**
     * 首页 最热文章
     * @return
     */
    @PostMapping("/hot")
    @Cache(name = "hotArticle")
    @LogAnnotation(module = "文章",operation = "获取最热文章")
    public Result hotArticle(){
        int limit = 5;
        return articleService.hotArticle(limit);
    }
    /**
     * 首页 最新文章
     * @return
     */
    @PostMapping("/new")
    @Cache(name = "newArticle")
    @LogAnnotation(module = "文章",operation = "获取最新文章")
    public Result newArticle(){
        int limit = 5;
        return articleService.newArticle(limit);
    }
    /**
     * 首页 文章归纳
     * @return
     */
    @PostMapping("/listArchives")
    @Cache(name = "listArchives")
    public Result listArchives(){

        return articleService.listArchives();
    }

    /**
     * 查看文章详情
     * @param id
     * @return
     */
    @PostMapping("view/{id}")
    @Cache(name = "viewArchives")
    public Result findArticleById(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam){
        return articleService.publish(articleParam);
    }

    @PostMapping("{id}")
    public Result articleById(@PathVariable("id") Long id){
        return articleService.findArticleById(id);
    }

}
