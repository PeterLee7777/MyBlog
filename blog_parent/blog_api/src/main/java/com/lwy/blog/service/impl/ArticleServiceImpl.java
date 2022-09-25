package com.lwy.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lwy.blog.entity.dos.Archives;
import com.lwy.blog.entity.param.ArticleParam;
import com.lwy.blog.entity.pojo.Article;
import com.lwy.blog.entity.pojo.ArticleBody;
import com.lwy.blog.entity.pojo.ArticleTag;
import com.lwy.blog.entity.pojo.Tag;
import com.lwy.blog.entity.vo.ArticleBodyVo;
import com.lwy.blog.entity.vo.ArticleVo;
import com.lwy.blog.entity.param.PageParams;
import com.lwy.blog.entity.vo.Result;
import com.lwy.blog.entity.vo.TagVo;
import com.lwy.blog.mapper.ArticleBodyMapper;
import com.lwy.blog.mapper.ArticleMapper;
import com.lwy.blog.mapper.ArticleTagMapper;
import com.lwy.blog.service.*;
import com.lwy.blog.utils.UserThreadLocal;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagService tagService;

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public Result listArticle(PageParams pageParams) {
        /**
         * 1.分页查询article数据库表
         */
        Page<Article> page = new Page<>(pageParams.getPage(), pageParams.getPageSize());
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();

        if(pageParams.getCategoryId() != null){
            queryWrapper.eq(Article::getCategoryId, pageParams.getCategoryId());
        }
        List<Long> articleIds = new ArrayList<>();
        if(pageParams.getTagId() != null){
            LambdaQueryWrapper<ArticleTag> queryWrapper1 = new LambdaQueryWrapper<>();
            queryWrapper1.eq(ArticleTag::getTagId, pageParams.getTagId());
            List<ArticleTag> tags = articleTagMapper.selectList(queryWrapper1);
            for (ArticleTag tag : tags) {
                articleIds.add(tag.getArticleId());
            }
        }
        if(articleIds.size() != 0){

            queryWrapper.in(Article::getId,articleIds);
        }
        //是否置顶进行排序（getWeight）
        //相当于 ： order by createDate desc （getCreateDate）
        queryWrapper.orderByDesc(Article::getWeight, Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, queryWrapper);
        List<Article> records = articlePage.getRecords();

        //能直接返回吗
        List<ArticleVo> articleVoList = copyList(records, true, true);
        return Result.success(articleVoList);
    }

    @Override
    public Result hotArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getViewCounts);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        //select id,title from article order by view_counts desc limit 5
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result newArticle(int limit) {
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Article::getCreateDate);
        queryWrapper.select(Article::getId, Article::getTitle);
        queryWrapper.last("limit " + limit);
        List<Article> articles = articleMapper.selectList(queryWrapper);
        return Result.success(copyList(articles, false, false));
    }

    @Override
    public Result listArchives() {
        List<Archives> archives = articleMapper.listArchives();
        return Result.success(archives);
    }

    @Autowired
    private ThreadService threadService;

    @Override
    public Result findArticleById(Long id) {
        /**
         * 1 根据id查询  文章 信息
         * 2 根据bodyId 和 categoryID 去做关联查询
         */

        Article article = articleMapper.selectById(id);

        ArticleVo articleVo = copy(article, true, true, true, true);
        //查看完文章了，新增阅读数，有没有问题呢?
        //查看完文章之后，本应该直接返回数据了，这时候做了一个更新操作，更新时加写锁，阻塞其他的读操作，性能就会比较低
        //更新增加了此次接口的耗时如果一旦更新出问题，不能影响查看文章的操作
        //线程池可以把更新操作扔到线程池中去执行，和主线程就不相关了

        threadService.updateViewCount(articleMapper,article);
        return Result.success(articleVo);
    }

    @Override
    public Result publish(ArticleParam articleParam) {
        /**
         * 1 article
         * 2 articleBody
         * 3 articleTag
         */
        Article article = new Article();
        article.setCommentCounts(0);
        article.setCreateDate(System.currentTimeMillis());
        article.setSummary(articleParam.getSummary());
        article.setTitle(articleParam.getTitle());
        article.setViewCounts(0);
        article.setWeight(Article.Article_Common);
        article.setAuthorId(UserThreadLocal.get().getId());
        article.setCategoryId(articleParam.getCategory().getId());
        article.setBodyId(0L);

        articleMapper.insert(article);
        Long articleId = article.getId();

        /**
         * 新增文章体
         */
        ArticleBody articleBody = new ArticleBody();
        articleBody.setArticleId(articleId);
        articleBody.setContent(articleParam.getBody().getContent());
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());

        articleBodyMapper.insert(articleBody);

        /**
         * 新增文章标签
         */
        List<TagVo> tags = articleParam.getTags();
        for (TagVo tag : tags) {
            ArticleTag articleTag = new ArticleTag();
            articleTag.setArticleId(articleId);
            articleTag.setTagId(tag.getId());
            articleTagMapper.insert(articleTag);
        }

        article.setBodyId(articleBody.getId());
        articleMapper.updateById(article);

        rocketMQTemplate.convertAndSend("blog-update-article",articleId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {

        ArrayList<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, isBody, isCategory));
        }
        return articleVoList;
    }

    private List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {

        ArrayList<ArticleVo> articleVoList = new ArrayList<>();
        for (Article record : records) {
            articleVoList.add(copy(record, isTag, isAuthor, false, false));
        }
        return articleVoList;
    }

    @Autowired
    private ArticleBodyMapper articleBodyMapper;

    @Autowired
    private CategoryService categoryService;

    private ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article, articleVo);

        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd hh:mm"));

        if (isTag) {
            Long articleId = article.getId();
            articleVo.setTags(tagService.findTagsByArticleId(articleId));
        }
        if (isAuthor) {
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(userService.findUserById01(authorId).getNickname());
        }
        if (isBody) {
            Long bodyId = article.getBodyId();
            articleVo.setBody(findArticleBodyById(bodyId));
        }
        if (isCategory) {
            Long categoryId = article.getCategoryId();
            articleVo.setCategory(categoryService.findCategoryById(categoryId));

        }
        return articleVo;
    }

    private ArticleBodyVo findArticleBodyById(Long bodyId) {

        ArticleBody articleBody = articleBodyMapper.selectById(bodyId);
        ArticleBodyVo articleBodyVo = new ArticleBodyVo();
        articleBodyVo.setContent(articleBody.getContent());
        return articleBodyVo;
    }

}
