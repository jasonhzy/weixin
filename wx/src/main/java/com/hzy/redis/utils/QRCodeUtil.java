package com.hzy.redis.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;

import javax.imageio.*;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.hzy.commons.exception.NotFoundException;
import com.hzy.commons.utils.MD5Util;
import com.hzy.commons.utils.StrUtil;
import org.w3c.dom.Element;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * 图片合成
 */
public class QRCodeUtil {

    private static final Integer DPI = 300;
    public static final String FORMAT_NAME = "jpg";
    public static final String TEMP_PATH = "/tmp/";

    /**
     * 生成二维码
     *
     * @param content
     * 链接
     * @param qrcode_width
     * 二维码宽
     * @param qrcode_height
     * 二维码高
     * @return 返回二维码图片
     * @throws Exception
     */
    private static BufferedImage createImage(String content, int qrcode_width, int qrcode_height) throws Exception {
        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.MARGIN, 1);
        BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, qrcode_width,
                qrcode_height, hints);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
    }

    /**
     * 合成图片
     *
     * @param url
     * 二维码链接
     * @param bgImgPath
     * 背景图片地址
     * @param codeWidth
     * 二维码宽度
     * @param codeHeight
     * 二维码高度
     * @return 合成的图片
     */
    public static BufferedImage compositeImage(String url, String bgImgPath, int codeWidth, int codeHeight,
            String content) {
        File file = new File(bgImgPath);
        if (!file.exists()) {
            throw new NotFoundException("模版文件不存在");
        }
        try {
            //URL bgUrl = new URL("http://example.cn/4a8e54cf5b374a8abe44632b08f31ff2");
            //Image backImage = ImageIO.read(bgUrl);

            Image backImage = ImageIO.read(file);

            int width = backImage.getWidth(null);
            int height = backImage.getHeight(null);
            int alphaType = BufferedImage.TYPE_INT_RGB;

            BufferedImage back = new BufferedImage(width, height, alphaType);
            // 画图
            Graphics2D g = back.createGraphics();
            g.drawImage(backImage, 0, 0, null);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alphaType));

            // 二维码图片
            BufferedImage qcodeImage = createImage(url, codeWidth, codeHeight);
            int x = width - codeWidth - 50;
            int y = height - codeHeight - 20;

            g.drawImage(qcodeImage, x, y, qcodeImage.getWidth(null), qcodeImage.getHeight(null), null);

            // 文字部分
            if (!StrUtil.empty(content)) {
                int fontSize = 45;
                String fontName = "Microsoft YaHei";
                int fontStyle = Font.PLAIN;
                Color fontColor = Color.BLACK;

                Font font = new Font(fontName, fontStyle, fontSize);
                g.setFont(font);
                g.setColor(fontColor);
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alphaType));
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                rh.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
                g.setRenderingHints(rh);

                FontMetrics metrics = g.getFontMetrics(font);
                // 文字在图片中的坐标 这里设置在中间
                int font_width = metrics.stringWidth(content);
                // 绘制文字
                g.drawString(content, (width - font_width) / 2, (height + codeHeight) / 2 - 120);
            }
            g.dispose();
            return back;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成带logo二维码
     *
     * @param url
     * @param bgImgPath
     * @param codeWidth
     * @param codeHeight
     * @throws Exception
     */
    public static String qrcode(String url, String bgImgPath, int codeWidth, int codeHeight, String fileName,
            String content) throws Exception {
        BufferedImage image = QRCodeUtil.compositeImage(url, bgImgPath, codeWidth, codeHeight, content);

        String tempFilePath = TEMP_PATH + fileName + "." + FORMAT_NAME;
        File file = new File(tempFilePath);
        ImageIO.write(image, FORMAT_NAME, file);
        if (file.exists()) {
            handleDpi(file, DPI);
        }
        return tempFilePath;
    }

    /**
     * 改变图片DPI
     *
     * @param file
     * @param dpi
     */
    private static void handleDpi(File file, int dpi) {
        try {
            BufferedImage bufferedImage = ImageIO.read(file);
            ImageWriter imageWriter = ImageIO.getImageWritersBySuffix(FORMAT_NAME).next();
            ImageWriteParam writeParams = imageWriter.getDefaultWriteParam();
            // only for jpg
            // writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // writeParams.setCompressionQuality(BufferedImage.TYPE_INT_RGB);

            IIOMetadata metadata = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bufferedImage),
                    writeParams);

            setDPI(metadata, dpi);

            ImageOutputStream output = ImageIO.createImageOutputStream(file);
            imageWriter.setOutput(output);
            imageWriter.write(null, new IIOImage(bufferedImage, null, metadata), writeParams);

            output.close();
            imageWriter.dispose();
        } catch (IIOInvalidTreeException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void setDPI(IIOMetadata metadata, int dpi) throws IIOInvalidTreeException {
        switch (FORMAT_NAME) {
            case "jpg":
            case "jpeg":
                Element tree = (Element) metadata.getAsTree("javax_imageio_jpeg_image_1.0");
                Element jfif = (Element) tree.getElementsByTagName("app0JFIF").item(0);
                jfif.setAttribute("Xdensity", Integer.toString(dpi));
                jfif.setAttribute("Ydensity", Integer.toString(dpi));
                jfif.setAttribute("resUnits", "1"); // density is dots per inch
                metadata.setFromTree("javax_imageio_jpeg_image_1.0", tree);
                break;
            case "png":
            default:
                // for PMG, it's dots per millimeter
                double dotsPerMilli = 1.0 * dpi / 10 / 2.54;
                IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
                horiz.setAttribute("value", Double.toString(dotsPerMilli));

                IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
                vert.setAttribute("value", Double.toString(dotsPerMilli));

                IIOMetadataNode dim = new IIOMetadataNode("Dimension");
                dim.appendChild(horiz);
                dim.appendChild(vert);

                IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
                root.appendChild(dim);

                metadata.mergeTree("javax_imageio_1.0", root);
                break;
        }
    }

    /**
     * 下载card
     *
     * @param response
     * @param filePath
     */
    public static void downloadFile(HttpServletResponse response, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new NotFoundException("下载文件不存在, 如需下载请重新创建");
        }
        try {
            String fileName = file.getName();
            // 读到流中
            InputStream inStream = new FileInputStream(filePath);// 文件的存放路径
            // 设置输出的格式
            response.reset();
            response.setContentType("application/octet-stream");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            // 循环取出流中的数据
            byte[] b = new byte[100];
            int len;
            while ((len = inStream.read(b)) > 0) {
                response.getOutputStream().write(b, 0, len);
            }
            inStream.close();
            // 下载后删除文件
            file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String url = "http://example.com";
        String tempName = "card";
        String bgImgPath = "http://example.com/mail.png";
        String fileName = MD5Util.getMD5Str("card");
        try {
            QRCodeUtil.qrcode(url, bgImgPath, 160, 160, fileName, null);
        } catch (NotFoundException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
