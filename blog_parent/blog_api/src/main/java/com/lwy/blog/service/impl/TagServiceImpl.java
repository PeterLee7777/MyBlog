package com.lwy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lwy.blog.entity.pojo.Tag;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.entity.vo.TagVo;
import com.lwy.blog.mapper.TagMapper;
import com.lwy.blog.service.TagService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.metrics.StartupStep;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Autowired
    private TagMapper tagMapper;



    @Override
    public List<TagVo> findTagsByArticleId(Long articleId) {
        //mybatis-plus无法进行多表查询
        List<Tag> tags = tagMapper.findTagsByArticleId(articleId);
        return copyList(tags);
    }

    @Override
    public Result hotTags(int limit) {
        /**
         * 1 标签所拥有的文章数量最多 就是最热标签
         * 2 查询 根据tag_id分组计数，从大到小
         */
        List<Long> tagIds = tagMapper.findHotTagIds(limit);

        if(CollectionUtils.isEmpty(tagIds)){
            return Result.success(Collections.emptyList());
        }
        //需求的是tagId 和 tagName Tag对象
        //select * from tag where id in (1,2,3,4)
        List<Tag> tags = tagMapper.findTagsByTagIds(tagIds);
        return Result.success(tags);
    }

    @Override
    public Result findAll() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<>());

        return Result.success(copyList(tags));
    }

    @Override
    public Result findAllDetail() {
        List<Tag> tags = tagMapper.selectList(new LambdaQueryWrapper<>());

        return Result.success(tags);
    }

    @Override
    public Result findAllDetailById(Long id) {
        Tag tag = tagMapper.selectById(id);

        return Result.success(tag);
    }

    private List<TagVo> copyList(List<Tag> tags) {
        ArrayList<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tags) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    private TagVo copy(Tag tag){
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
}
