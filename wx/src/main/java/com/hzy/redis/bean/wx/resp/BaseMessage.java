package com.hzy.redis.bean.wx.resp;

import com.hzy.redis.constant.MsgTypeEnum;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 消息基类（公众帐号 -> 普通用户）
 * 
 * @author jason hu
 *
 */
public class BaseMessage {
    // 接收方帐号（收到的OpenID）
    @XStreamAlias("ToUserName")
    private String ToUserName;
    // 开发者微信号
    @XStreamAlias("FromUserName")
    private String FromUserName;
    // 消息创建时间 （整型）
    @XStreamAlias("CreateTime")
    private long CreateTime;
    // 消息类型（text/image/location/link/video/shortvideo）
    @XStreamAlias("MsgType")
    private MsgTypeEnum MsgType;

    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    public long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(long createTime) {
        CreateTime = createTime;
    }

    public MsgTypeEnum getMsgType() {
        return MsgType;
    }

    public void setMsgType(MsgTypeEnum msgType) {
        MsgType = msgType;
    }

    @Override
    public String toString() {
        return "BaseMessage [ToUserName=" + ToUserName + ", FromUserName=" + FromUserName + ", CreateTime=" + CreateTime
                + ", MsgType=" + MsgType + "]";
    }
}
