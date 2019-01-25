package com.hzy.redis.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.hzy.commons.response.ResultData;
import com.hzy.commons.response.ResultUtil;
import com.hzy.redis.bean.NoticeRequest;
import com.hzy.redis.bean.WxRequest;
import com.hzy.redis.constant.WxConstant;
import com.hzy.redis.service.WxService;

@RestController
@RequestMapping(value = "/wx")
public class WxController {

    private static final Logger logger = LoggerFactory.getLogger(WxController.class);

    @Resource
    private WxService wxService;

    @GetMapping(value = "/auth")
    public void auth(HttpServletRequest request, HttpServletResponse response) {
        try {
            String redirectUrl = WxConstant.REDIRECT_URL;
            String request_url = wxService.getAuthUrlForCode(URLEncoder.encode(redirectUrl, "UTF-8"));
            response.sendRedirect(request_url);
        } catch (UnsupportedEncodingException e) {
            logger.error("redis oauth redirect url encode error: " + e.getMessage());
        } catch (IOException e) {
            logger.error("redis oauth send redirect url error: " + e.getMessage());
        }
    }

    @GetMapping(value = "/openid")
    public ResponseEntity<ResultData> getOpenid(HttpServletRequest request, HttpServletResponse response,
                                                @RequestParam("code") String code) {

        String openid = wxService.getOpenId(code);
        Map<String, String> result = new HashMap<>();
        result.put("openid", openid);
        return ResultUtil.success(result);
    }

    @PostMapping(value = "/accessToken")
    public ResponseEntity<ResultData> getAccessToken(HttpServletRequest request) {
        String accessToken = wxService.getWxAccessToken();
        Map<String, String> result = new HashMap<>();
        result.put("accessToken", accessToken);
        return ResultUtil.success(result);
    }

    @GetMapping(value = "/jsapiTicket")
    public ResponseEntity<ResultData> getJsapiTicket(HttpServletRequest request) {
        String jsapiTicket = wxService.getJsapiTicket();
        Map<String, String> result = new HashMap<>();
        result.put("ticket", jsapiTicket);
        return ResultUtil.success(result);
    }

    @PostMapping(value = "/info")
    public ResponseEntity<ResultData> getOpenid(HttpServletRequest request, @RequestBody WxRequest wxRequest) {
        Map<String, String> user = wxService.getOAuthUser(wxRequest);
        return ResultUtil.success(user);
    }

    @PostMapping(value = "/sendMsg")
    public ResponseEntity<ResultData> sendMsg(@RequestBody NoticeRequest noticeRequest) {
        wxService.sendMsg(noticeRequest);
        return ResultUtil.success("发送成功");
    }

    @PostMapping(value = "/upload")
    public ResponseEntity<ResultData> upload(HttpServletRequest request, @RequestBody WxRequest wxRequest) {
        Map<String, String> media = wxService.upload(wxRequest);
        return ResultUtil.success(media);
    }

    @PostMapping(value = "/customservice")
    public ResponseEntity<ResultData> customserviceMsg(HttpServletRequest request, @RequestBody WxRequest wxRequest) {
        wxService.customserviceMsg(wxRequest);
        return ResultUtil.success("发送成功");
    }

    @PostMapping(value = "/menu/create")
    public ResponseEntity<ResultData> createMenu(HttpServletRequest request, @RequestBody(required = false) WxRequest wxRequest) {
        wxService.createMenu(wxRequest);
        return ResultUtil.success("创建成功");
    }

    @GetMapping(value = "/menu/del")
    public ResponseEntity<ResultData> delMenu(HttpServletRequest request) {
        wxService.delMenu();
        return ResultUtil.success("删除成功");
    }

    @RequestMapping(value = "/{appId:^(?![0-9]+$)(?![a-zA-Z]+$)[\\dA-Za-z]{8,32}$}")
    public ResponseEntity<String> auth(@PathVariable(value = "appId") String token, HttpServletRequest request) {
        String method = request.getMethod().toLowerCase();
        String resp = "";
        switch (method){
            case "get":
                // 时间戳
                String timestamp = request.getParameter("timestamp");
                // 随机数
                String nonce = request.getParameter("nonce");

                // 微信加密签名，signature结合了开发者填写的token参数和请求中的timestamp参数、nonce参数。
                String signature = request.getParameter("signature");
                // 随机字符串
                String echostr = request.getParameter("echostr");

                // 通过检验signature对请求进行校验，若校验成功则原样返回echostr，否则接入失败
                if (wxService.checkSignature(token, timestamp, nonce, signature)) {
                    resp = echostr;
                }
                break;
            case "post":
                resp = wxService.reply(token, request);
                break;
            default:
                return new ResponseEntity<>(method, HttpStatus.OK);
        }
        logger.info("redis msg: {}", resp);
        return new ResponseEntity<>(resp, HttpStatus.OK);
    }
}
