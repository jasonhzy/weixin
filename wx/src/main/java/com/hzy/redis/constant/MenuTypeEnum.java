package com.hzy.redis.constant;

public enum MenuTypeEnum {
    CLICK, // click菜单
    VIEW, // url菜单
    SCANCODE_WAITMSG, // 扫码带提示
    SCANCODE_PUSH, // 扫码推事件
    PIC_SYSPHOTO, // 系统拍照发图
    PIC_PHOTO_OR_ALBUM, // 拍照或者相册发图
    PIC_WEIXIN, // 微信相册发图
    LOCATION_SELECT; // 发送位置
}
