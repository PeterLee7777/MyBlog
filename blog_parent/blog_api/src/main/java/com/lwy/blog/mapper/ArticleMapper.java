package com.lwy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwy.blog.entity.dos.Archives;
import com.lwy.blog.entity.pojo.Article;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ArticleMapper extends BaseMapper<Article> {
    List<Archives> listArchives();
}
