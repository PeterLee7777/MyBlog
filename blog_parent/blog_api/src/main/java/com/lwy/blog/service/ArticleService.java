package com.lwy.blog.service;

import com.lwy.blog.entity.param.ArticleParam;
import com.lwy.blog.entity.param.PageParams;
import com.lwy.blog.entity.vo.Result;

public interface ArticleService {

    /**
     * 分页查询 文章列表
     * @param pageParams
     * @return
     */
    Result listArticle(PageParams pageParams);

    /**
     * 最热文章
     * @param limit
     * @return
     */
    Result hotArticle(int limit);

    /**
     * 最新文章
     * @param limit
     * @return
     */
    Result newArticle(int limit);

    /**
     *  文章归纳
     * @return
     */
    Result listArchives();

    /**
     * 查看文章详情
     * @param id
     * @return
     */
    Result findArticleById(Long id);

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    Result publish(ArticleParam articleParam);
}
