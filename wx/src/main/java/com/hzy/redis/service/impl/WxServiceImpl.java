package com.hzy.redis.service.impl;

import java.io.File;
import java.util.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONException;
import com.hzy.commons.exception.NotFoundException;
import com.hzy.commons.exception.ParamInvalidException;
import com.hzy.commons.exception.ParamMissingException;
import com.hzy.commons.exception.WxErrorException;
import com.hzy.commons.utils.DateUtil;
import com.hzy.commons.utils.HttpClientUtil;
import com.hzy.commons.utils.StrUtil;
import com.hzy.redis.RedisService;
import com.hzy.redis.bean.NoticeRequest;
import com.hzy.redis.bean.WxRequest;
import com.hzy.redis.bean.wx.menu.*;
import com.hzy.redis.constant.MenuTypeEnum;
import com.hzy.redis.constant.MsgEventEnum;
import com.hzy.redis.constant.MsgTypeEnum;
import com.hzy.redis.constant.WxConstant;
import com.hzy.redis.service.EventDispatcher;
import com.hzy.redis.service.MsgDispatcher;
import com.hzy.redis.service.WxService;
import com.hzy.redis.bean.wx.WxTempMsg;
import com.hzy.redis.utils.wxUtil.MessageUtil;
import com.hzy.redis.utils.wxUtil.SHA1;
import com.hzy.redis.utils.wxUtil.WXBizMsgCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

@Service
public class WxServiceImpl implements WxService {

    private static final Logger logger = LoggerFactory.getLogger(WxServiceImpl.class);

    @Resource
    private RedisService redisService;

    /**
     * get auth url for code
     * 
     * @param redirectUrl
     * @return
     */
    @Override
    public String getAuthUrlForCode(String redirectUrl) {
        return WxConstant.OAUTH_URL + "?appid=" + WxConstant.APP_ID + "&redirect_uri=" + redirectUrl
                + "&response_type=code" + "&scope=snsapi_base" + "&state=example#wechat_redirect";
    }

    private String getAuthUrlForOpenid(String code) {
        return WxConstant.OAUTH_TOKEN_URL + "?appid=" + WxConstant.APP_ID + "&secret=" + WxConstant.APP_SECRET
                + "&code=" + code + "&grant_type=authorization_code";
    }

    private String getAccessTokenUrl() {
        return WxConstant.ACCESS_TOKEN_URL + "?grant_type=client_credential&appid=" + WxConstant.APP_ID + "&secret="
                + WxConstant.APP_SECRET;
    }

    private String getJsapiTicketUrl(String accessToken) {
        return WxConstant.JSAPI_TICKET_URL + "?access_token=" + accessToken + "&type=jsapi";
    }

    private String getOAuthUserUrl(String accessToken, String openid) {
        return WxConstant.WX_USERINFO + "?access_token=" + accessToken + "&openid=" + openid + "&lang=zh_CN";
    }

    private String getMsgUrl(String accessToken) {
        return WxConstant.WX_MSG_URL + "?access_token=" + accessToken;
    }

    private String getTempMediaUrl(String accessToken, String type) {
        return WxConstant.WX_TEMPORARY_MEDIA_UPLOAD + "?access_token=" + accessToken + "&type=" + type;
    }

    private String getCustomServiceUrl(String accessToken){
        return WxConstant.WX_CUSTOM_SERVICE + "?access_token=" + accessToken;
    }

    private String getCreateMenuUrl(String accessToken){
        return WxConstant.WX_CREATE_MENU + "?access_token=" + accessToken;
    }

    private String getDelMenuUrl(String accessToken){
        return WxConstant.WX_DEL_MENU + "?access_token=" + accessToken;
    }

    /**
     * 获取openid
     *
     * @param code
     * @return
     */
    @Override
    public String getOpenId(String code) {
        String url = getAuthUrlForOpenid(code);
        String res = HttpClientUtil.get(url);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") || !json.containsKey("openid")) {
            logger.error("get openid error: " + res);
            throw new WxErrorException("获取openid error：" + res);
        }
        logger.info("openid: " + res);
        return StrUtil.toStr(json.getString("openid"));
    }

    /**
     * 获取access_token
     *
     * @return
     */
    public String getAccessToken() {
        String tokenUrl = getAccessTokenUrl();

        // 凭证有效时间，避免频繁请求
        String tokenKey = WxConstant.WX_ACCESS_TOKEN_CACHE_KEY;
        boolean bool = redisService.exists(tokenKey);
        if (bool) {
            return StrUtil.toStr(redisService.get(tokenKey));
        }

        String res = HttpClientUtil.get(tokenUrl);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") || !json.containsKey("access_token")) {
            logger.error("get access_token error: " + res);
            throw new WxErrorException("获取access_token error：" + res);
        }
        // 返回：{"access_token":"ACCESS_TOKEN","expires_in":7200}
        int expires_in = json.getIntValue("expires_in");
        String accessToken = json.getString("access_token");
        redisService.set(tokenKey, accessToken, expires_in - 200);
        return accessToken;
    }

    /**
     * 获取access token
     *
     * @return
     */
    @Override
    public String getWxAccessToken() {
        return getAccessToken();
    }

    @Override
    public String getJsapiTicket() {
        // 凭证有效时间，避免频繁请求
        String ticketKey = WxConstant.WX_JSAPI_TICKET_CACHE_KEY;
        boolean bool = redisService.exists(ticketKey);
        if (bool) {
            return StrUtil.toStr(redisService.get(ticketKey));
        }

        String accessToken = getAccessToken();
        String url = getJsapiTicketUrl(accessToken);
        String res = HttpClientUtil.get(url);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.containsKey("errcode"))) {
            logger.error("get jsapi ticket error: " + res);
            throw new WxErrorException("获取jsapi ticket，error：" + res);
        }
        // 返回：{ "errcode":0, "errmsg":"ok", "ticket":"xxxxx", "expires_in":7200 }
        int expires_in = json.getIntValue("expires_in");
        String ticket = json.getString("ticket");
        redisService.set(ticketKey, ticket, expires_in - 200);
        return ticket;
    }

    /**
     * 获取用户基本信息
     *
     * @param wxRequest
     * @return
     */
    @Override
    public Map<String, String> getOAuthUser(WxRequest wxRequest) {
        String openid = StrUtil.toStr(wxRequest.getOpenid());
        if (StrUtil.empty(openid)) {
            throw new ParamMissingException("openid");
        }

        String accessToken = getAccessToken();
        String userUrl = getOAuthUserUrl(accessToken, openid);
        String res = HttpClientUtil.get(userUrl);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode")) {
            logger.error("get oauth user info error: " + res);
            throw new WxErrorException("获取用户信息，error：" + res);
        }

        Map<String, String> user = new HashMap<>();
        String subscribe = StrUtil.toStr(json.getString("subscribe"));
        if ("1".equals(subscribe)) {
            user.put("nickname", json.getString("nickname"));
            user.put("country", json.getString("country"));
            user.put("province", json.getString("province"));
            user.put("city", json.getString("city"));
            user.put("headimgurl", json.getString("headimgurl"));
            user.put("subscribe_time", json.getString("subscribe_time"));
        }
        //subscribe: 0 未关注 1 已关注
        user.put("subscribe", subscribe);
        user.put("openid", json.getString("openid"));
        return user;
    }

    private WxTempMsg getWithdrawMsg(String openid, int totalFee) {
        float amount = totalFee / 100.0f;

        TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
        // 根据具体模板参数组装
        params.put("first", WxTempMsg.item("您的提现申请已经处理完毕", null));
        params.put("keyword1", WxTempMsg.item(amount + "元", null));
        params.put("keyword2", WxTempMsg.item(StrUtil.date2String(DateUtil.FORMAT_DATETIME, new Date()), null));
        params.put("remark", WxTempMsg.item("请注意查收,谢谢！", null));

        WxTempMsg wxTempMsg = new WxTempMsg();
        wxTempMsg.setTemplate_id(WxConstant.WX_WITHDRAW_TEMP_ID);
        wxTempMsg.setTouser(openid);
        wxTempMsg.setData(params);
        return wxTempMsg;
    }

    private WxTempMsg getApplyMsg(String openid) {
        TreeMap<String, TreeMap<String, String>> params = new TreeMap<String, TreeMap<String, String>>();
        // 根据具体模板参数组装
        params.put("first", WxTempMsg.item("恭喜，您的申请已审核通过", null));
        params.put("keyword1", WxTempMsg.item("审核成功", null));
        params.put("keyword2", WxTempMsg.item(StrUtil.date2String(DateUtil.FORMAT_DATETIME, new Date()), null));
        params.put("remark", WxTempMsg.item("您的申请已通过", null));

        WxTempMsg wxTempMsg = new WxTempMsg();
        wxTempMsg.setTemplate_id(WxConstant.WX_APPLY_TEMP_ID);
        wxTempMsg.setTouser(openid);
        wxTempMsg.setData(params);
        return wxTempMsg;
    }

    /**
     * 发送微信提醒
     *
     * @param noticeRequest
     */
    @Override
    public void sendMsg(NoticeRequest noticeRequest) {
        String openid = StrUtil.toStr(noticeRequest.getOpenid());
        if (StrUtil.empty(openid)) {
            throw new ParamMissingException("openid", "为必填参数");
        }

        WxTempMsg wxTempMsg = null;
        String type = noticeRequest.getType();
        switch (type) {
            case "withdraw":
                wxTempMsg = getWithdrawMsg(openid, noticeRequest.getTotalFee());
                break;
            case "apply":
                wxTempMsg = getApplyMsg(openid);
                break;
            default:
                throw new NotFoundException("没有此消息提醒类型: " + type);
        }
        String accessToken = getAccessToken();
        String url = getMsgUrl(accessToken);
        String res = HttpClientUtil.post(url, JSONObject.toJSONString(wxTempMsg));
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
            logger.error("send temp msg error: " + res);
            throw new WxErrorException("发送模板消息，error：" + res);
        }
    }

    /**
     * 创建默认菜单
     *
     * @return
     */
    private Menu getMenu() {
        CommonButton v1 = new CommonButton();
        v1.setType(MenuTypeEnum.VIEW.name().toLowerCase());
        v1.setName("官网首页");
        v1.setUrl("http://example.cn");

        CommonButton c1 = new CommonButton();
        c1.setType(MenuTypeEnum.LOCATION_SELECT.name().toLowerCase());
        c1.setName("发送位置");
        c1.setKey("rselfmenu_2_0");

        CommonButton v2 = new CommonButton();
        v2.setType(MenuTypeEnum.VIEW.name().toLowerCase());
        v2.setName("联系我们");
        v2.setUrl("http://example.cn/");

        CommonButton c2 = new CommonButton();
        c2.setType(MenuTypeEnum.CLICK.name().toLowerCase());
        c2.setName("公司简介");
        c2.setKey("profile");

        //一级菜单(有二级菜单)
        ComplexMenu cm = new ComplexMenu();
        cm.setName("关于我们");
        cm.setSub_button(new BasicButton[] { v2, c2 });

        Menu menu = new Menu();
        menu.setButton(new BasicButton[] { v1, c1, cm });
        return menu;
    }

    @Override
    public void createMenu(WxRequest wxRequest){
        String menu = wxRequest == null ? null : wxRequest.getMenu();
        if(StrUtil.isBlank(menu)){
            menu = JSONObject.toJSONString(getMenu());
        }
        logger.info("menu info: " + menu);
        try{
            JSONObject.parseObject(menu);
        }catch (JSONException e){
            throw new ParamInvalidException("menu", "不满足json格式");
        }

        String accessToken = getAccessToken();
        String url = getCreateMenuUrl(accessToken);
        String res = HttpClientUtil.post(url, menu);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
            logger.error("create menu error: " + res);
            throw new WxErrorException("创建菜单，error：" + res);
        }
    }

    @Override
    public void delMenu(){
        String accessToken = getAccessToken();
        String url = getDelMenuUrl(accessToken);
        String res = HttpClientUtil.get(url);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
            logger.error("del menu error: " + res);
            throw new WxErrorException("删除菜单，error：" + res);
        }
    }

    @Override
    public boolean checkSignature(String token, String timestamp, String nonce, String signature) {
        String[] arr = new String[] { token, timestamp, nonce };
        // 将token、timestamp、nonce三个参数进行字典排序
        Arrays.sort(arr);
        StringBuilder sb = new StringBuilder();
        for (String v : arr) {
            sb.append(v);
        }
        // 进行sha1加密
        String signStr = null;
        try {
            signStr = SHA1.getSha1(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (null == signStr) {
            return false;
        }
        // 将sha1加密后的字符串可与signature对比
        return signStr.equals(signature);
    }

    @Override
    public String reply(String token, HttpServletRequest request) {
        // 时间戳
        String timestamp = request.getParameter("timestamp");
        // 随机数
        String nonce = request.getParameter("nonce");
        // 进行解密
        String msgSignature = request.getParameter("msg_signature");
        // 加密方式
        String encryptType = request.getParameter("encrypt_type");
        // String signature = request.getParameter("signature");
        // String openid = request.getParameter("openid");

        WXBizMsgCrypt msgCrypt = new WXBizMsgCrypt(token, WxConstant.ENCODING_AESKEY, WxConstant.APP_ID);
        Map<String, Object> msg = null;
        if ("aes".equals(encryptType.toLowerCase())) { // 以安全模式提交的数据
            String xmlMsg = msgCrypt.decryptMsg(msgSignature, timestamp, nonce, MessageUtil.streamToStr(request));
            logger.info("redis msg string: {}", xmlMsg);
            msg = MessageUtil.parseXml(xmlMsg);
        } else { // 以明文模式提交过来的,不需要解密
            msg = MessageUtil.parseXml(request);
        }

        // 发送方帐号
        String fromUserName = StrUtil.toStr(msg.get("FromUserName"));
        // 开发者微信号openid
        String toUserName = StrUtil.toStr(msg.get("ToUserName"));
        // 事件
        String event = StrUtil.toStr(msg.get("Event"));
        // 消息类型
        String msgType = StrUtil.toStr(msg.get("MsgType"));
        if (null == msgType || "".equals(msgType)) {
            throw new WxErrorException("No MsgType");
        }
        // 事件推送
        MsgEventEnum eventTypeEnum = MsgEventEnum.valueOf(event.toUpperCase());
        // 消息类型
        MsgTypeEnum msgTypeEnum = MsgTypeEnum.valueOf(msgType.toUpperCase());

        String resp = null;
        if (msgType.equals(MsgTypeEnum.EVENT)) {
            resp = EventDispatcher.processEvent(msgCrypt, eventTypeEnum, fromUserName, toUserName, timestamp, nonce);
        } else {
            resp = MsgDispatcher.processMessage(msgCrypt, msgTypeEnum, fromUserName, toUserName, timestamp, nonce);
        }
        return resp;
    }

    @Override
    public Map<String, String> upload(WxRequest wxRequest) {
        String openid = StrUtil.toStr(wxRequest.getOpenid());
        if (StrUtil.empty(openid)) {
            throw new ParamMissingException("openid");
        }

        String filePath = "/Users/jason/Desktop/test.jpg";
        File file = new File(filePath);
        if(!file.exists()){
            throw new NotFoundException("上传文件不存在");
        }

        String accessToken = getAccessToken();
        //type媒体文件类型，分别有图片（image）、语音（voice）、视频（video）和缩略图（thumb）
        String type = "image";
        String res = HttpClientUtil.upload(getTempMediaUrl(accessToken, type), "media", file);
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
            throw new WxErrorException("上传临时素材，error：" + res);
        }

        Map<String, String> resp = new HashMap<>();
        //resp.put("mediaId", "OrIQsnI-FgtBWUk33Uaf2xwbzMT6zsXkhgZPy5TVeJNsKSOjtSeqm1elxL_3gnIp");
        resp.put("mediaId", StrUtil.toStr(json.getString("media_id")));
        return resp;

    }

    @Override
    public void customserviceMsg(WxRequest wxRequest){
        String openid = StrUtil.toStr(wxRequest.getOpenid());
        if (StrUtil.empty(openid)) {
            throw new ParamMissingException("openid");
        }

        String accessToken = getAccessToken();
        String url = getCustomServiceUrl(accessToken);

        Map<String, String> contentMap = new HashMap<>();
        contentMap.put("media_id", "OrIQsnI-FgtBWUk33Uaf2xwbzMT6zsXkhgZPy5TVeJNsKSOjtSeqm1elxL_3gnIp");

        Map<String, Object> params = new HashMap<>();
        params.put("touser", openid);
        params.put("msgtype", "image");
        params.put("image", contentMap);

        String res = HttpClientUtil.post(url, JSONObject.toJSONString(params));
        JSONObject json = JSONObject.parseObject(res);
        if (json.containsKey("errcode") && !"0".equals(json.getString("errcode"))) {
            logger.error("send temp msg error: " + res);
            throw new WxErrorException("发送客服消息，error：" + res);
        }
    }
}
