package com.hzy.redis.utils.wxUtil;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.hzy.redis.bean.wx.resp.*;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;


public class MessageUtil {

    public static String PREFIX_CDATA = "<![CDATA[";
    public static String SUFFIX_CDATA = "]]>";

    public static String streamToStr(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 解析微信发来的请求(xml)
     *
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Map<String, Object> parseXml(HttpServletRequest request) {
        // 将解析结果存储在HashMap中
        Map<String, Object> map = new HashMap<>();
        try {
            // 从request中取得输入流
            InputStream inputStream = request.getInputStream();
            // 读取输入流
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputStream);
            // 得到xml根元素
            Element root = document.getRootElement();
            // 遍历所有子节点
            findAndPutElement(root, map);
            // 释放资源
            inputStream.close();
            inputStream = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * XML格式字符串转换为Map
     *
     * @param strXML XML字符串
     * @return XML数据转换后的Map
     * @throws Exception
     */
    public static Map<String, Object> parseXml(String strXML) {
        Map<String, Object> map = new HashMap<>();
        try {
            Document document = DocumentHelper.parseText(strXML);
            Element root = document.getRootElement();
            findAndPutElement(root, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @SuppressWarnings("unchecked")
    private static void findAndPutElement(Element rootElement, Map<String, Object> map) {
        if (null == rootElement){
            return;
        }
        List<Element> elementList = rootElement.elements();
        for (Element e : elementList) {
            if (null != map.get(e.getName())){

            }
            Object tempObj = null;
            if (e.elements().size() < 1){
                tempObj = e.getStringValue();
            } else {
                Map<String, Object> innerMap = new HashMap<String, Object>();
                findAndPutElement(e, innerMap);
                tempObj = innerMap;
            }

            if (null == map.get(e.getName())){
                map.put(e.getName(), tempObj);
            } else if (map.get(e.getName()) instanceof List){
                List<Object> obj = (List<Object>) map.get(e.getName());
                obj.add(tempObj);
            } else{
                List<Object> objList = new LinkedList<Object>();
                objList.add(map.get(e.getName()));
                objList.add(tempObj);
                map.put(e.getName(), objList);
            }
        }
    }

    public static <T> T xml2Bean(String xmlStr, Class<T> cls) {
        XStream xstream = new XStream(new DomDriver());
        xstream.setClassLoader(cls.getClassLoader());
        xstream.processAnnotations(cls);
        xstream.alias("redis", cls);
        T obj = (T) xstream.fromXML(xmlStr);
        return obj;
    }

    public static Map<String, String> xml2Map(String xmlStr) {
        XStream xstream = new XStream(new DomDriver());
        xstream.alias("xml", Map.class);
        return (Map<String, String>) xstream.fromXML(xmlStr);
    }

    /**
     * 扩展xstream，使其支持CDATA块
     * 由于xstream框架本身并不支持CDATA块的生成，下面代码是对xtream做了扩展，
     * 使其支持在生成xml各元素值时添加CDATA块。
     */
    private static XStream xstream = new XStream(new XppDriver() {
        public HierarchicalStreamWriter createWriter(Writer out) {
            return new PrettyPrintWriter(out) {
                // 对所有xml节点的转换都增加CDATA标记
                boolean cdata = true;
                @SuppressWarnings("unchecked")
                public void startNode(String name, Class clazz) {
                    super.startNode(name, clazz);
                }
                protected void writeText(QuickWriter writer, String text) {
                    if (cdata) {
                        writer.write(PREFIX_CDATA);
                        writer.write(text);
                        writer.write(SUFFIX_CDATA);
                    } else {
                        super.writeText(writer, text);
                    }
                }
            };
        }
    });

    /**
     * 文本消息对象转换成xml
     *
     * @param textMessage
     * 文本消息对象
     * @return xml
     */
    public static String textMessageToXml(TextMessage textMessage) {
        xstream.alias("xml", textMessage.getClass());
        return xstream.toXML(textMessage);
    }

    /**
     * 图片消息对象转换成xml
     *
     * @param imageMessage
     * 文本消息对象
     * @return xml
     */
    public static String imageMessageToXml(ImageMessage imageMessage) {
        xstream.alias("xml", imageMessage.getClass());
        xstream.processAnnotations(Image.class);
        return xstream.toXML(imageMessage);
    }

    /**
     * 图文消息对象转换成xml
     *
     * @param newsMessage
     * 图文消息对象
     * @return xml
     */
    public static String newsMessageToXml(NewsMessage newsMessage) {
        xstream.alias("xml", newsMessage.getClass());
        xstream.processAnnotations(Article.class);
        xstream.alias("item", new Article().getClass());
        return xstream.toXML(newsMessage);
    }
}
