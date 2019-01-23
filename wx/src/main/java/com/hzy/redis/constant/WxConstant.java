package com.hzy.redis.constant;

public class WxConstant {
    // redirect url for redis
    public static String REDIRECT_URL = "http://example.cn/wx/openid";

    // wx info
    public static String APP_ID = "wxxxxxxxxx";
    public static String APP_SECRET = "xxxxxxxxxxx";
    public static String ENCODING_AESKEY = "xxxxxxxxxxxxxxxxx";
    // oath2获取code
    public static String OAUTH_URL = "https://open.weixin.qq.com/connect/oauth2/authorize";
    // oath2获取openid
    public static String OAUTH_TOKEN_URL = "https://api.weixin.qq.com/sns/oauth2/access_token";
    // 获取access_token
    public static String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token";
    // 获取用户信息
    public static String WX_USERINFO = "https://api.weixin.qq.com/cgi-bin/user/info";
    // 模板消息
    public static String WX_MSG_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send";
    // 新增临时素材
    public static String WX_TEMPORARY_MEDIA_UPLOAD = "https://api.weixin.qq.com/cgi-bin/media/upload";
    // 客服消息
    public static String WX_CUSTOM_SERVICE = "https://api.weixin.qq.com/cgi-bin/message/custom/send";
    // 模板消息
    public static String WX_WITHDRAW_TEMP_ID = "82PQxvD0pWc7cdPRxP7dg9cWVDD_SQKW4QeZrJ2t9x0";
    public static String WX_APPLY_TEMP_ID = "1iV3Qis1rqg11YKd8jJb1zwuhNq7SuJc95oLlKOeBJg";
    // 创建/删除菜单
    public static String WX_CREATE_MENU = "https://api.weixin.qq.com/cgi-bin/menu/create";
    public static String WX_DEL_MENU = "https://api.weixin.qq.com/cgi-bin/menu/delete";

    // redis access token cache key
    public static String WX_ACCESS_TOKEN_CACHE_KEY = "WX_CACHE_ACCESS_TOKEN";
}
