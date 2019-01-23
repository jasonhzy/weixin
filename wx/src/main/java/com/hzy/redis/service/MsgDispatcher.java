package com.hzy.redis.service;

import com.hzy.redis.bean.wx.resp.*;
import com.hzy.redis.constant.MsgTypeEnum;
import com.hzy.redis.utils.wxUtil.MessageUtil;
import com.hzy.redis.utils.wxUtil.WXBizMsgCrypt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ClassName: MsgDispatcher
 * 
 * @Description: 消息业务处理分发器
 */
public class MsgDispatcher {

    public static String processMessage(WXBizMsgCrypt msgCrypt, MsgTypeEnum msgTypeEnum,
                                        String fromUserName, String toUserName, String timestamp, String nonce) {
        String resp = null;
        switch (msgTypeEnum) { // 被动回复用户信息
            case TEXT: // 回复文本消息
                TextMessage textMessage = new TextMessage();
                textMessage.setToUserName(fromUserName);
                textMessage.setFromUserName(toUserName);
                textMessage.setCreateTime(new Date().getTime());
                textMessage.setContent("hello");
                textMessage.setMsgType(MsgTypeEnum.TEXT);
                // 将消息对象转换成xml
                String xmlText = MessageUtil.textMessageToXml(textMessage);
                resp = msgCrypt.encryptMsg(xmlText, timestamp, nonce);
            case IMAGE:
                Image image = new Image();
                image.setMediaId("e4NC3kRagaGb69w8ikRGgTxtKtlWHmDuJzA7rzCYygk");

                ImageMessage imageMessage = new ImageMessage();
                imageMessage.setToUserName(fromUserName);
                imageMessage.setFromUserName(toUserName);
                imageMessage.setCreateTime(new Date().getTime());
                imageMessage.setMsgType(MsgTypeEnum.IMAGE);
                imageMessage.setImage(image);

                // 将消息对象转换成xml
                String xmlImage = MessageUtil.imageMessageToXml(imageMessage);
                resp = msgCrypt.encryptMsg(xmlImage, timestamp, nonce);
                break;
            case NEWS:
                NewsMessage newsMessage = new NewsMessage();
                newsMessage.setToUserName(fromUserName);
                newsMessage.setFromUserName(toUserName);
                newsMessage.setCreateTime(new Date().getTime());
                newsMessage.setMsgType(MsgTypeEnum.NEWS);

                List<Article> list = new ArrayList<>();
                Article article = new Article();
                article.setTitle("感谢关注，请绑定账户");
                article.setUrl("http://example.cn");
                article.setPicUrl("http://example.cn");
                article.setDescription("desc");
                list.add(article);

                newsMessage.setArticles(list);
                newsMessage.setArticleCount(list.size());
                // 将消息对象转换成xml
                String xmlNews = MessageUtil.newsMessageToXml(newsMessage);
                resp = msgCrypt.encryptMsg(xmlNews, timestamp, nonce);
                break;
            case VOICE:
                break;
            case SHORTVIDEO:
                break;
            case LINK:
                break;
            case LOCATION:
                break;
            default:
                break;
        }
        return resp;
    }
}
