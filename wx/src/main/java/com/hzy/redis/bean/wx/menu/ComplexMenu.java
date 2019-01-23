package com.hzy.redis.bean.wx.menu;

public class ComplexMenu  extends BasicButton {

    private BasicButton[] sub_button;

    public BasicButton[] getSub_button() {
        return sub_button;
    }

    public void setSub_button(BasicButton[] sub_button) {
        this.sub_button = sub_button;
    }
}