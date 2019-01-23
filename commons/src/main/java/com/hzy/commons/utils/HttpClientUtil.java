package com.hzy.commons.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtil {

    public static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    public static String get(String url) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseBody = "";
        HttpGet httpget = new HttpGet(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httpget.setConfig(requestConfig);
        logger.info(" http request url : " + url);
        try {
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                logger.info(" http request status : " + response.getStatusLine());
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            logger.error("{}", e);
        } catch (ParseException e) {
            logger.error("{}", e);
        } catch (IOException e) {
            logger.error("{}", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        logger.info(" http request response : " + responseBody);
        return responseBody;
    }

    public static String post(String url, String jsonStr) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String responseBody = "";
        HttpPost httppost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(30000).setConnectTimeout(30000).build();
        httppost.setConfig(requestConfig);
        logger.info(" http request url : " + url);
        try {
            if (null != jsonStr) {
                StringEntity jsonEntity = new StringEntity(jsonStr, "UTF-8");
                httppost.setEntity(jsonEntity);
            }
            CloseableHttpResponse response = httpclient.execute(httppost);
            logger.info(" http request status : " + response.getStatusLine());
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    responseBody = EntityUtils.toString(entity, "UTF-8");
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            logger.error("{}", e);
        } catch (UnsupportedEncodingException e) {
            logger.error("{}", e);
        } catch (IOException e) {
            logger.error("{}", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                logger.error("{}", e);
            }
        }
        logger.info(" http request response : " + responseBody);
        return responseBody;
    }

    public static String upload(String url, String type, File file) {
        HttpPost post = new HttpPost(url);
        // 设置参数
        RequestConfig.Builder customReqConf = RequestConfig.custom();
        customReqConf.setConnectTimeout(30000);
        customReqConf.setSocketTimeout(30000);
        post.setConfig(customReqConf.build());

        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.addBinaryBody(type, file);
        //FileBody fb = new FileBody(file);
        //multipartEntityBuilder.addPart(type, fb);

        HttpEntity entity = multipartEntityBuilder.build();
        post.setEntity(entity);

        String resp = "";
        CloseableHttpClient client = null;
        try {
            // 建立一个sslcontext，这里我们信任任何的证书。
            SSLContext context = getTrustAllSSLContext();
            // 建立socket工厂
            SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context);
            // 建立连接器
            client = HttpClients.custom().setSSLSocketFactory(factory).build();

            CloseableHttpResponse response = client.execute(post);
            try {
                resp = EntityUtils.toString(response.getEntity(), "UTF-8");
            } finally {
                response.close();
            }
        } catch (Exception e) {
            logger.error("error for upload file ", e);
        } finally {
            post.releaseConnection();
            try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resp;
    }

    private static SSLContext getTrustAllSSLContext() throws Exception {
        SSLContext context = SSLContexts.custom().loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                // 这一句就是信任任何的证书，当然你也可以去验证微信服务器的真实性
                return true;
            }
        }).build();
        return context;
    }
}
