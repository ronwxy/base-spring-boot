package cn.jboost.springboot.common.security;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * 加密，解密工具类 —— Base64, AES, MD5
 * @author ray4work@126.com
 * @date 2018/5/21 10:00
 */
public class EncryptUtil {

    private final static String CHARSET_UTF8 = "UTF-8";
    private final static String ALGORITH_AES = "AES"; //AES算法
    private final static String ALGORITH_PATTERN_AES = "AES/ECB/PKCS5Padding"; //"算法/模式/补码方式"

    /**
     * base64 编码
     * @param data
     * @return
     */
    public static String base64Encode(byte[] data){
        return Base64.encodeBase64String(data);
    }

    /**
     * base64 编码
     * @param data
     * @return
     */
    public static String base64Encode(String data){
        return base64Encode(data.getBytes());
    }

    /**
     * Base64 解码  */
    public static byte[] base64Decode(byte[] data) {
        return Base64.decodeBase64(data);
    }

    /**
     * Base64 解码  */
    public static byte[] base64Decode(String data) {
        return base64Decode(data.getBytes());
    }

    /**
     * 结合base64实现aes加密
     * @param data 待加密字符串
     * @return 获取md5后转为base64
     * @throws Exception
     */
    public static String aesEncryptToBase64(String data, String secretKey) throws Exception{
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITH_AES);
        Cipher cipher = Cipher.getInstance(ALGORITH_PATTERN_AES);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return base64Encode(encrypted);
    }

    /**
     * 将base 64 code AES解密
     * @param data 待解密的base 64 code
     * @param secretKey 解密密钥
     * @return 解密后的string
     * @throws Exception
     */
    public static String aesDecryptFromBase64(String data, String secretKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(secretKey.getBytes(), ALGORITH_AES);
        Cipher cipher = Cipher.getInstance(ALGORITH_PATTERN_AES);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
        byte[] original = cipher.doFinal(base64Decode(data));
        return new String(original);
    }

    /**
     * 获取byte[]的md5值
     * @param data byte[]
     * @return md5
     */
    public static String md5(byte[] data) {
        return DigestUtils.md5Hex(data);
    }

    /**
     * 获取byte[]的md5值
     * @param data byte[]
     * @return md5
     */
    public static String md5(String data) {
        return DigestUtils.md5Hex(data);
    }
}
