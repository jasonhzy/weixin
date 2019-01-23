package com.hzy.redis.bean.wx.resp;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * 消息响应之图片消息：
 *
 */
@XStreamAlias("ImageMessage")
public class ImageMessage extends BaseMessage {
    // 回复的消息内容
    @XStreamAlias("Image")
    private Image Image;

    public Image getImage() {
        return Image;
    }

    public void setImage(Image mediaId) {
        Image = mediaId;
    }

    @Override
    public String toString() {
        return super.toString() + "\nImageMessage [Image=" + Image + "]";
    }

}
