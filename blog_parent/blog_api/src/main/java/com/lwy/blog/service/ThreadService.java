package com.lwy.blog.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lwy.blog.mapper.ArticleMapper;
import com.lwy.blog.entity.pojo.Article;
import org.aspectj.lang.annotation.Around;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ThreadService {




    @Async("taskExecutor")
    public void updateViewCount(ArticleMapper articleMapper,Article article){

        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(article.getViewCounts() + 1);
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Article::getId,article.getId());
        queryWrapper.eq(Article::getViewCounts,article.getViewCounts());
        int update = articleMapper.update(articleUpdate, queryWrapper);
//        while (update == 0) {
//            queryWrapper.eq(Article::getViewCounts,article.getViewCounts() + 1);
//            articleMapper.update(articleUpdate,)
//        }
        try {
            //睡眠5秒 证明不会影响主线程的使用
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}