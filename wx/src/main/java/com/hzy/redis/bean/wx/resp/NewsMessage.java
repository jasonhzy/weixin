package com.hzy.redis.bean.wx.resp;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.List;

/**
 * @desc  : 图文消息
 *
 */
@XStreamAlias("NewsMessage")
public class NewsMessage extends BaseMessage {
    // 图文消息个数，限制为10条以内
    @XStreamAlias("ArticleCount")
    private int ArticleCount;
    // 多条图文消息信息，默认第一个item为大图
    @XStreamAlias("Articles")
    private List<Article> Articles;

    public int getArticleCount() {
        return ArticleCount;
    }

    public void setArticleCount(int articleCount) {
        ArticleCount = articleCount;
    }

    public List<Article> getArticles() {
        return Articles;
    }

    public void setArticles(List<Article> articles) {
        Articles = articles;
    }
}