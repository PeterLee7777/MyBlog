package com.lwy.blog.service;

import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.entity.vo.TagVo;

import java.util.List;

public interface TagService {

    List<TagVo> findTagsByArticleId(Long articleId);

    Result hotTags(int limit);

    Result findAll();

    Result findAllDetail();

    Result findAllDetailById(Long id);
}
