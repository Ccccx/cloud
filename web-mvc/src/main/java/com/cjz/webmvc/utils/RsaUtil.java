package com.cjz.webmvc.utils;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-27 10:44
 */

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * RSA安全编码组件
 *
 * @author chengjz
 * @version 1.0
 */
public class RsaUtil {
    /**
     * 非对称加密密钥算法
     */
    public static final String KEY_ALGORITHM_RSA = "RSA";

    /**
     * 公钥
     */
    private static final String RSA_PUBLIC_KEY = "RSAPublicKey";

    /**
     * 私钥
     */
    private static final String RSA_PRIVATE_KEY = "RSAPrivateKey";

    /**
     * RSA密钥长度
     * 默认1024位，
     * 密钥长度必须是64的倍数，
     * 范围在512至65536位之间。
     */
    private static final int KEY_SIZE = 1024;
    private static BouncyCastleProvider bouncyCastleProvider = null;

    static {
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }

    /**
     * 防止内存泄露, 手动指定防止Security顺序异常时加解密出现问题
     */
    public static synchronized BouncyCastleProvider getBouncyCastleProvider() {
        if (bouncyCastleProvider == null) {
            bouncyCastleProvider = new BouncyCastleProvider();
        }
        return bouncyCastleProvider;
    }

    public static String encryptByPublicKey(String data, String key) throws Exception {
        final byte[] dataBytes = encryptByPublicKey(data.getBytes(StandardCharsets.UTF_8.name()), decodeBase64(key));
        return encodeBase64(dataBytes);
    }

    /**
     * 公钥加密
     *
     * @param data 待加密数据
     * @param key  公钥
     * @return byte[] 加密数据
     * @throws Exception ig
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);

        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm(), getBouncyCastleProvider());

        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            int outputSize = cipher.getOutputSize(data.length);
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0, remainSize = 0;
            while ((remainSize = data.length - i * blockSize) > 0) {
                int inputLen = Math.min(remainSize, blockSize);
                cipher.doFinal(data, i * blockSize, inputLen, raw, i * outputSize);
                i++;
            }
            return raw;
        }
        return cipher.doFinal(data);
    }


    /**
     * 公钥解密
     *
     * @param data 待解密数据
     * @param key  公钥
     * @return byte[] 解密数据
     * @throws Exception ig
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] key) throws Exception {
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(key);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);

        // 生成公钥
        PublicKey publicKey = keyFactory.generatePublic(x509KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm(), getBouncyCastleProvider());

        cipher.init(Cipher.DECRYPT_MODE, publicKey);

        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data 待加密数据
     * @param key  私钥
     * @return byte[] 加密数据
     * @throws Exception ig
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);

        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据加密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());

        cipher.init(Cipher.ENCRYPT_MODE, privateKey);

        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            int outputSize = cipher.getOutputSize(data.length);
            int leavedSize = data.length % blockSize;
            int blocksSize = leavedSize != 0 ? data.length / blockSize + 1
                    : data.length / blockSize;
            byte[] raw = new byte[outputSize * blocksSize];
            int i = 0, remainSize = 0;
            while ((remainSize = data.length - i * blockSize) > 0) {
                int inputLen = Math.min(remainSize, blockSize);
                cipher.doFinal(data, i * blockSize, inputLen, raw, i * outputSize);
                i++;
            }
            return raw;
        }
        return cipher.doFinal(data);
    }

    public static String decryptByPrivateKey(String data, String key) throws Exception {
        final byte[] dataBytes = decryptByPrivateKey(decodeBase64(data), decodeBase64(key));
        return new String(dataBytes, StandardCharsets.UTF_8.name());
    }

    /**
     * 私钥解密
     *
     * @param data 待解密数据
     * @param key  私钥
     * @return byte[] 解密数据
     * @throws Exception ig
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] key) throws Exception {
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(key);

        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM_RSA);

        // 生成私钥
        PrivateKey privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm(), getBouncyCastleProvider());

        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        int blockSize = cipher.getBlockSize();
        if (blockSize > 0) {
            ByteArrayOutputStream bout = new ByteArrayOutputStream(64);
            int j = 0;
            while (data.length - j * blockSize > 0) {
                bout.write(cipher.doFinal(data, j * blockSize, blockSize));
                j++;
            }
            return bout.toByteArray();
        }
        return cipher.doFinal(data);
    }

    public static String getPublicKey(Map<String, String> keyMap) {
        return keyMap.get(RSA_PUBLIC_KEY);
    }

    /**
     * 取得公钥
     *
     * @param keyMap 密钥Map
     * @return byte[] 公钥
     * @throws Exception ig
     */
    public static byte[] getPublicKeyByte(Map<String, String> keyMap) {
        return decodeBase64(getPublicKey(keyMap));
    }

    public static String getPrivateKey(Map<String, String> keyMap) {
        return keyMap.get(RSA_PRIVATE_KEY);
    }

    /**
     * 取得私钥
     *
     * @param keyMap 密钥Map
     * @return byte[] 私钥
     * @throws Exception ig
     */
    public static byte[] getPrivateKeyByte(Map<String, String> keyMap)
            throws Exception {
        return decodeBase64(keyMap.get(RSA_PRIVATE_KEY));
    }


    /**
     * 初始化密钥
     *
     * @param seed 种子
     * @return Map 密钥Map
     * @throws Exception ig
     */
    public static Map<String, String> initKey(byte[] seed) throws Exception {
        // 实例化密钥对生成器
        KeyPairGenerator keyPairGen = KeyPairGenerator
                .getInstance(KEY_ALGORITHM_RSA);

        // 初始化密钥对生成器
        keyPairGen.initialize(KEY_SIZE, new SecureRandom(seed));

        // 生成密钥对
        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        // 封装密钥
        Map<String, String> keyMap = new HashMap<>(2);

        keyMap.put(RSA_PUBLIC_KEY, encodeBase64(publicKey));
        keyMap.put(RSA_PRIVATE_KEY, encodeBase64(privateKey));

        return keyMap;
    }


    /**
     * 初始化密钥
     *
     * @return Map 密钥Map
     * @throws Exception ig
     */
    public static Map<String, String> initKey() throws Exception {
        return initKey(UUID.randomUUID().toString().getBytes());
    }

    /**
     * 初始化密钥
     *
     * @param seed 种子
     * @return Map 密钥Map
     * @throws Exception ig
     */
    public static Map<String, String> initKey(String seed) throws Exception {
        return initKey(seed.getBytes());
    }


    private static PublicKey getPublicRsaKey(String key) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodeBase64(key));
        return kf.generatePublic(x509);
    }

    private static PrivateKey getPrivateRsaKey(String key) throws Exception {
        KeyFactory kf = KeyFactory.getInstance(KEY_ALGORITHM_RSA);
        PKCS8EncodedKeySpec pkgs8 = new PKCS8EncodedKeySpec(decodeBase64(key));
        return kf.generatePrivate(pkgs8);
    }


    /**
     * 使用url安全的base64算法变体对二进制数据进行编码
     *
     * @param key key
     * @return base64字符串
     */
    private static String encodeBase64(Key key) {
        return Base64.encodeBase64URLSafeString(key.getEncoded());
    }

    private static String encodeBase64(byte[] data) {
        return Base64.encodeBase64URLSafeString(data);
    }

    /**
     * 返回原始字节数组
     *
     * @param key key
     * @return 字节数组
     */
    private static byte[] decodeBase64(String key) {
        return Base64.decodeBase64(key);
    }

}
