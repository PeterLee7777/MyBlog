package com.lwy.blog.service;

import com.lwy.blog.entity.vo.CategoryVo;
import com.lwy.blog.entity.vo.Result;

import java.util.List;

public interface CategoryService {
    CategoryVo findCategoryById(Long categoryId);

    Result findAll();


    Result findAllDetail();

    Result findAllDetailById(Long id);

}
