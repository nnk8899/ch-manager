package com.xh.mgr.util;
import javax.crypto.Cipher;  
import javax.crypto.spec.IvParameterSpec;  
import javax.crypto.spec.SecretKeySpec;  
  
import sun.misc.BASE64Decoder;  
import sun.misc.BASE64Encoder;  
  
public class AESUtil {  
    private String sKey = "abcdef0123456789";  
    private String ivParameter = "0123456789abcdef";  
    private static AESUtil instance = null;  
  
    private AESUtil() {  
  
    }  
  
    public static AESUtil getInstance() {  
        if (instance == null)  
            instance = new AESUtil();  
        return instance;  
    }  
  
    public String encrypt(String sSrc,String key){  
        String result = "";  
        try {  
            Cipher cipher;  
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            byte[] raw = key.getBytes();  
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());// 使用CBC模式，需要一个向量iv，可增加加密算法的强度  
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);  
            byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));  
            result = new BASE64Encoder().encode(encrypted);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }   
        return result;  
                  
    }  
  
    public String decrypt(String sSrc,String key){  
        try {  
            byte[] raw = key.getBytes("ASCII");  
            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");  
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");  
            IvParameterSpec iv = new IvParameterSpec(ivParameter.getBytes());  
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);  
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(encrypted1);  
            String originalString = new String(original, "utf-8");  
            return originalString;  
        } catch (Exception ex) {  
            ex.printStackTrace();  
            return null;  
        }  
    }  
}  