/**
 * 对公众平台发送给公众账号的消息加解密示例代码.
 * 
 * @copyright Copyright (c) 1998-2014 Tencent Inc.
 */

// ------------------------------------------------------------------------

package com.hzy.redis.utils.wxUtil;

import com.hzy.commons.exception.WxErrorException;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 * SHA1 class
 *
 * 计算公众平台的消息签名接口.
 */
public class SHA1 {
	/**
	 * 用SHA1算法生成安全签名
	 * @param token 票据
	 * @param timestamp 时间戳
	 * @param nonce 随机字符串
	 * @param encrypt 密文
	 * @return 安全签名
	 * @throws AesException 
	 */
	public static String getMsgSHA1(String token, String timestamp, String nonce, String encrypt) {
		try {
			String[] array = new String[] { token, timestamp, nonce, encrypt };
			StringBuffer sb = new StringBuffer();
			// 字符串排序
			Arrays.sort(array);
			for (int i = 0; i < 4; i++) {
				sb.append(array[i]);
			}
			String str = sb.toString();
			return getSha1(str);
		} catch (Exception e) {
			e.printStackTrace();
			throw new WxErrorException(AesException.getMessage(AesException.ComputeSignatureError));
		}
	}

	/**
	 * SHA1签名生成
	 *
	 * @param str
	 * @return
	 */
	public static String getSha1(String str) throws Exception{
		if (str == null || str.length() == 0) {
			return null;
		}
		MessageDigest md = MessageDigest.getInstance("SHA-1");
		md.update(str.getBytes());
		byte[] digest = md.digest();

		StringBuffer hexstr = new StringBuffer();
		String shaHex = "";
		for (int i = 0; i < digest.length; i++) {
			shaHex = Integer.toHexString(digest[i] & 0xFF);
			if (shaHex.length() < 2) {
				hexstr.append(0);
			}
			hexstr.append(shaHex);
		}
		return hexstr.toString();
	}
}
