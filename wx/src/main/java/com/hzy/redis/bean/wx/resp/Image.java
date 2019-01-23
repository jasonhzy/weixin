package com.hzy.redis.bean.wx.resp;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("Image")
public class Image {
    @XStreamAlias("MediaId")
    private String mediaId;

    public String getMediaId() {
        return mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }
}
