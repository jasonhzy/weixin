package com.hzy.redis.service;

import com.hzy.redis.bean.NoticeRequest;
import com.hzy.redis.bean.WxRequest;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public interface WxService {

    String getAuthUrlForCode(String redirectUrl);

    String getOpenId(String code);

    String getWxAccessToken();

    Map<String, String> getOAuthUser(WxRequest wxRequest);

    void sendMsg(NoticeRequest noticeRequest);

    Map<String, String> upload(WxRequest wxRequest);

    void customserviceMsg(WxRequest wxRequest);

    boolean checkSignature(String token, String timestamp, String nonce, String signature);

    String reply(String token, HttpServletRequest request);

    void createMenu(WxRequest wxRequest);

    void delMenu();
}
