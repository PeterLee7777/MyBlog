package com.lwy.blog.service;

import com.lwy.blog.entity.param.CommentParam;
import com.lwy.blog.entity.vo.Result;

public interface CommentsService {

    /**
     * 根据文章id查询评论记录
     * @param articleId
     * @return
     */
    Result commentsByArticleId(Long articleId);

    Result comment(CommentParam commentParam);
}
