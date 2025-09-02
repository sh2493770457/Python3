package dev.ctool.burp.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * 加密工具类 - 提供各种加密解密功能
 */
public class CryptoUtils {
    
    static {
        // 添加BouncyCastle提供者
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }
    }
    
    /**
     * 计算哈希值
     */
    public static String calculateHash(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hash = digest.digest(data);
        return bytesToHex(hash);
    }
    
    /**
     * AES加密
     */
    public static String aesEncrypt(String plaintext, String key, String mode, String padding, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        }
        
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * AES解密
     */
    public static String aesDecrypt(String ciphertext, String key, String mode, String padding, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/" + mode + "/" + padding);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
        
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        }
        
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * DES加密
     */
    public static String desEncrypt(String plaintext, String key, String mode, String padding, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/" + mode + "/" + padding);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "DES");
        
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        }
        
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * DES解密
     */
    public static String desDecrypt(String ciphertext, String key, String mode, String padding, String iv) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/" + mode + "/" + padding);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "DES");
        
        if ("ECB".equals(mode)) {
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
        } else {
            IvParameterSpec ivSpec = new IvParameterSpec(iv.getBytes(StandardCharsets.UTF_8));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
        }
        
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * RSA密钥对生成
     */
    public static KeyPair generateRSAKeyPair(int keySize) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(keySize);
        return keyGen.generateKeyPair();
    }
    
    /**
     * RSA加密
     */
    public static String rsaEncrypt(String plaintext, String publicKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyStr);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PublicKey publicKey = keyFactory.generatePublic(keySpec);
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        
        byte[] encrypted = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encrypted);
    }
    
    /**
     * RSA解密
     */
    public static String rsaDecrypt(String ciphertext, String privateKeyStr) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(ciphertext));
        return new String(decrypted, StandardCharsets.UTF_8);
    }
    
    /**
     * HMAC签名
     */
    public static String hmacSign(String data, String key, String algorithm) throws Exception {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance(algorithm);
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
        mac.init(keySpec);
        byte[] signature = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(signature);
    }
    
    /**
     * 字节数组转十六进制字符串
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * 十六进制字符串转字节数组
     */
    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                                + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }
    
    /**
     * 生成随机密钥
     */
    public static String generateRandomKey(int length) {
        SecureRandom random = new SecureRandom();
        byte[] key = new byte[length];
        random.nextBytes(key);
        return bytesToHex(key);
    }
    
    /**
     * 生成随机IV
     */
    public static String generateRandomIV(int length) {
        SecureRandom random = new SecureRandom();
        byte[] iv = new byte[length];
        random.nextBytes(iv);
        return bytesToHex(iv);
    }
    
    /**
     * 密钥长度调整
     */
    public static String adjustKeyLength(String key, int targetLength) {
        if (key.length() == targetLength) {
            return key;
        } else if (key.length() > targetLength) {
            return key.substring(0, targetLength);
        } else {
            StringBuilder sb = new StringBuilder(key);
            while (sb.length() < targetLength) {
                sb.append('0');
            }
            return sb.toString();
        }
    }
}