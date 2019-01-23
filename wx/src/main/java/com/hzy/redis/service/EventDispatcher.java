package com.hzy.redis.service;


import com.hzy.redis.constant.MsgEventEnum;
import com.hzy.redis.utils.wxUtil.WXBizMsgCrypt;

/**
 * ClassName: EventDispatcher
 * 
 * @Description: 事件消息业务分发器
 */
public class EventDispatcher {

    public static String processEvent(WXBizMsgCrypt msgCrypt, MsgEventEnum eventTypeEnum,
                                      String fromUserName, String toUserName, String timestamp, String nonce) {
        String resp = null;
        switch (eventTypeEnum) {
            case SUBSCRIBE:
                break;
            case UNSUBSCRIBE:
                break;
            case LOCATION:
                break;
            case CLICK:
                break;
            case VIEW:
                break;
            case SCAN:
                break;
            default:
                break;
        }
        return resp;
    }
}
