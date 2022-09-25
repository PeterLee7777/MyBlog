package com.lwy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwy.blog.entity.dos.Archives;
import com.lwy.blog.entity.pojo.Article;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author 李文阳
 */
@Component
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取文章列表
     * @return 文章列表
     */
    List<Archives> listArchives();
}
