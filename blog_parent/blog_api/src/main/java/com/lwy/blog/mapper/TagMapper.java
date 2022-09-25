package com.lwy.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lwy.blog.entity.pojo.Tag;

import java.util.List;

/**
 * @author 李文阳
 */
public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 根据文章id查询标签列表
     * @param articleId 文章id
     * @return 标签列表
     */
    List<Tag> findTagsByArticleId(Long articleId);

    /**
     * 查询最热的标签 前n条
     * @param limit 条数
     * @return 标签id列表
     */
    List<Long> findHotTagIds(int limit);

    /**
     * 根据标签id查询标签
     * @param tagIds 标签id
     * @return 标签列表
     */
    List<Tag> findTagsByTagIds(List<Long> tagIds);
}
