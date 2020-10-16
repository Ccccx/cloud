package com.cjz.webmvc.rsa;

import com.cjz.webmvc.utils.RsaUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.crypto.cipher.CryptoCipher;
import org.apache.commons.crypto.random.CryptoRandom;
import org.apache.commons.crypto.random.CryptoRandomFactory;
import org.apache.commons.crypto.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * @author chengjz
 * @version 1.0
 * @since 2020-09-27 10:26
 */
@Slf4j
public class RsaTest {
	/**
	 * Converts String to UTF8 bytes
	 *
	 * @param input the input string
	 * @return UTF8 bytes
	 */
	private static byte[] getUTF8Bytes(final String input) {
		return input.getBytes(StandardCharsets.UTF_8);
	}

	/**
	 * Converts ByteBuffer to String
	 *
	 * @param buffer input byte buffer
	 * @return the converted string
	 */
	private static String asString(final ByteBuffer buffer) {
		final ByteBuffer copy = buffer.duplicate();
		final byte[] bytes = new byte[copy.remaining()];
		copy.get(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	@Test
	public void t1() throws Exception {
		byte[] key = new byte[16];
		byte[] iv = new byte[32];
		final Properties properties = new Properties();
		properties.put(CryptoRandomFactory.CLASSES_KEY, CryptoRandomFactory.RandomProvider.OPENSSL.getClassName());
		// Gets the 'CryptoRandom' instance.
		try (CryptoRandom random = CryptoRandomFactory.getCryptoRandom(properties)) {
			// Show the actual class (may be different from the one requested)
			System.out.println(random.getClass().getCanonicalName());
			// Generate random bytes and places them into the byte arrays.
			random.nextBytes(key);
			random.nextBytes(iv);
		}
		// Show the generated output
		System.out.println(Arrays.toString(key));
		System.out.println(Arrays.toString(iv));
	}

	@Test
	public void t2() throws Exception {
		final SecretKeySpec key = new SecretKeySpec(getUTF8Bytes("1234567890123456"), "AES");
		final IvParameterSpec iv = new IvParameterSpec(getUTF8Bytes("1234567890123456"));
		final Properties properties = new Properties();
		//Creates a CryptoCipher instance with the transformation and properties.
		final String transform = "AES/CBC/PKCS5Padding";
		final ByteBuffer outBuffer;
		final int bufferSize = 1024;
		final int updateBytes;
		final int finalBytes;
		try (
				CryptoCipher encipher = Utils.getCipherInstance(transform, properties)) {

			final ByteBuffer inBuffer = ByteBuffer.allocateDirect(bufferSize);
			outBuffer = ByteBuffer.allocateDirect(bufferSize);
			inBuffer.put(getUTF8Bytes("hello world!"));

			inBuffer.flip(); // ready for the cipher to read it
			// Show the data is there
			System.out.println("inBuffer=" + asString(inBuffer));

			// Initializes the cipher with ENCRYPT_MODE,key and iv.
			encipher.init(Cipher.ENCRYPT_MODE, key, iv);
			// Continues a multiple-part encryption/decryption operation for byte buffer.
			updateBytes = encipher.update(inBuffer, outBuffer);
			System.out.println(updateBytes);

			// We should call do final at the end of encryption/decryption.
			finalBytes = encipher.doFinal(inBuffer, outBuffer);
			System.out.println(finalBytes);
		}

		outBuffer.flip(); // ready for use as decrypt
		final byte[] encoded = new byte[updateBytes + finalBytes];
		outBuffer.duplicate().get(encoded);
		System.out.println(Arrays.toString(encoded));

		// Now reverse the process
		try (CryptoCipher decipher = Utils.getCipherInstance(transform, properties)) {
			decipher.init(Cipher.DECRYPT_MODE, key, iv);
			final ByteBuffer decoded = ByteBuffer.allocateDirect(bufferSize);
			decipher.update(outBuffer, decoded);
			decipher.doFinal(outBuffer, decoded);
			decoded.flip(); // ready for use
			System.out.println("decoded=" + asString(decoded));
		}
	}

	@Test
	public void t3() throws Exception {
		final Map<String, String> keyMap = RsaUtil.initKey();
		keyMap.forEach((k, v) -> System.out.println(k + " : " + v));
		String str = source();
		// 公钥加密
		final byte[] encrypt = RsaUtil.encryptByPublicKey(str.getBytes(StandardCharsets.UTF_8.name()), RsaUtil.getPublicKeyByte(keyMap));
		final String encryptStr = new String(encrypt, StandardCharsets.UTF_8.name());
		System.out.println("加密后字符串: " + encryptStr);
		// 私钥解密
		final byte[] decrypt = RsaUtil.decryptByPrivateKey(encrypt, RsaUtil.getPrivateKeyByte(keyMap));
		final String decryptStr = new String(decrypt, StandardCharsets.UTF_8.name());
		System.out.println("解密后字符串: " + decryptStr);
	}

	@Test
	public void t4() throws Exception {
		final Map<String, String> keyMap = RsaUtil.initKey();
		keyMap.forEach((k, v) -> System.out.println(k + " : " + v));
		String str = source();
		// 公钥加密
		final String encryptStr = RsaUtil.encryptByPublicKey(str, RsaUtil.getPublicKey(keyMap));
		System.out.println("加密后字符串: " + encryptStr);
		// 私钥解密
		System.out.println("解密后字符串: " + RsaUtil.decryptByPrivateKey(encryptStr, RsaUtil.getPrivateKey(keyMap)));
	}

	@Test
	public void t5() throws Exception {
		String str = "{\"deviceName\":\"T9003\",\"typeFlag\":\"BUS\",\"others\":{\"deviceName\":\"T9003\",\"TIMESTAMP\":\"1601199088309\",\"typeFlag\":\"BUS\"},\"pageSize\":10,\"pageNum\":1}";

		String position = StringUtils.repeat("=", 40);
		StringBuilder sb = new StringBuilder();
		sb.append(position);
		// 公钥加密
		final String encryptStr = RsaUtil.encryptByPublicKey(str, PUBLIC_KEY);
		System.out.println("加密后字符串: " + encryptStr);
		// 私钥解密
		System.out.println("解密后字符串: " + RsaUtil.decryptByPrivateKey(encryptStr, PRIVATE_KEY));
	}

	@Test
	public void t6() {
		String str = "{\"deviceName\":\"T9003\",\"typeFlag\":\"BUS\",\"others\":{\"deviceName\":\"T9003\",\"TIMESTAMP\":\"1601199088309\",\"typeFlag\":\"BUS\"},\"pageSize\":10,\"pageNum\":1}";

		String position = "\n" + StringUtils.repeat("=", 60) + "\n";
		StringBuilder sb = new StringBuilder();
		sb.append(position);
		sb.append("PUBLIC_KEY: \n");
		//sb.append(StringUtils.());
		log.info(sb.toString());
	}

	public static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCv8r3OTDyD9-gKytmtAjACzI1Fkq0gMe49JqZQqPzfJuvhGg4eRPFr7SfvDAyZ0uXhucx44W7Y_xutk2L4GrT5pETQNRow4XIC0XN9CNokAS3OXZb7qjBgjsdjfoDGzFWgus5H7B0eyqTUcIny4P54T4sbuldM_en3WNM6jf4SXwIDAQAB";
	public static String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAK_yvc5MPIP36ArK2a0CMALMjUWSrSAx7j0mplCo_N8m6-EaDh5E8WvtJ-8MDJnS5eG5zHjhbtj_G62TYvgatPmkRNA1GjDhcgLRc30I2iQBLc5dlvuqMGCOx2N-gMbMVaC6zkfsHR7KpNRwifLg_nhPixu6V0z96fdY0zqN_hJfAgMBAAECgYBFpJLdd86SO8ukjfNIKDABw559ddfCARF4MfatwZ3J5DmEcZA6Cfb3Tbk7RO0t68zjImxdQnniOWPmugp4AvYzmv-fLPEX72OvETxLEJdbGqCsgH6FGPYmrtDH1LnTDIwQsz0jZ4GzzdsqAj3owm65qRvWcn3coUmuQwB9UYMxQQJBAOypcEnU6evljahNG-1UqWFSZKG7EqbsfpykBPzH2HjExDxzjCCasjFH9TGfjhSpN1LjwW8S7zttqypqujOhvMECQQC-U0bi53neArNLjHz6W7jyGHClLO5__R9Qny-TbOUBtV-JnH9XsuLzB8uE4sFdErF3rq9D-dddokMa7MJQyvcfAkBfE79J2iDUJZfgioHyufWGZMyK8RYQO6iuE3LxfvQnqJ1Rz_X2z09RXyBRfFFfWZAhW-DFV-0Gigue8du2jH7BAkEAmoAdzjqcSmYkyFJ-bfohc531a5DC93Eyh48B3wqKCM4EhUBee4HDDAiq5O_yiiyPlhlfO65Ib0E2elfyEgYBfwJAVSo2myiFVptQjy1DFlPcZ_lchTyHfu6zsgpRJQDBWVN80srFmB2P1475udUyTHDkHF-LnfQWOAq9vyfoHNagAQ";

	public static String source() {
		return "import com.google.common.collect.ImmutableMap;\n" +
				"import org.apache.commons.codec.DecoderException;\n" +
				"import org.apache.commons.codec.binary.Hex;\n" +
				"import org.apache.commons.lang3.StringUtils;\n" +
				"import org.apache.commons.lang3.time.DateFormatUtils;\n" +
				"\n" +
				"import javax.crypto.BadPaddingException;\n" +
				"import javax.crypto.Cipher;\n" +
				"import javax.crypto.IllegalBlockSizeException;\n" +
				"import javax.crypto.NoSuchPaddingException;\n" +
				"import java.math.BigInteger;\n" +
				"import java.nio.charset.StandardCharsets;\n" +
				"import java.security.InvalidKeyException;\n" +
				"import java.security.InvalidParameterException;\n" +
				"import java.security.KeyFactory;\n" +
				"import java.security.KeyPair;\n" +
				"import java.security.KeyPairGenerator;\n" +
				"import java.security.NoSuchAlgorithmException;\n" +
				"import java.security.PrivateKey;\n" +
				"import java.security.PublicKey;\n" +
				"import java.security.SecureRandom;\n" +
				"import java.security.interfaces.RSAPrivateKey;\n" +
				"import java.security.interfaces.RSAPublicKey;\n" +
				"import java.security.spec.InvalidKeySpecException;\n" +
				"import java.security.spec.RSAPrivateKeySpec;\n" +
				"import java.security.spec.RSAPublicKeySpec;\n" +
				"import java.util.Date;\n" +
				"import java.util.Map;\n" +
				"import java.util.Objects;\n" +
				"\n" +
				"/**\n" +
				" * RSA算法加密/解密工具类。\n" +
				" *\n" +
				" * @author zhangshibo\n" +
				" */\n" +
				"public class RSAUtil {\n" +
				"\n" +
				"    /**\n" +
				"     * 参数名称：模\n" +
				"     */\n" +
				"    public static final String MODULUS_NAME = \"modulus\";\n" +
				"\n" +
				"    /**\n" +
				"     * 参数名称：指数\n" +
				"     */\n" +
				"    public static final String EXPONENT_NAME = \"exponent\";\n" +
				"\n" +
				"    /**\n" +
				"     * 算法名称\n" +
				"     */\n" +
				"    public static final String ALGORITHM = \"RSA\";\n" +
				"\n" +
				"    /**\n" +
				"     * 密钥大小\n" +
				"     */\n" +
				"    public static final int KEY_SIZE = 1024;\n" +
				"\n" +
				"    private static KeyPairGenerator keyPairGen;\n" +
				"    private static KeyFactory keyFactory;\n" +
				"    /**\n" +
				"     * 缓存的密钥对。\n" +
				"     */\n" +
				"    private static KeyPair oneKeyPair = null;\n" +
				"\n" +
				"\n" +
				"    static {\n" +
				"        try {\n" +
				"            keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);\n" +
				"            keyFactory = KeyFactory.getInstance(ALGORITHM);\n" +
				"        } catch (NoSuchAlgorithmException ex) {\n" +
				"            throw new IllegalStateException(ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    private RSAUtils() {\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 生成并返回RSA密钥对。\n" +
				"     */\n" +
				"    private static synchronized KeyPair generateKeyPair() {\n" +
				"        if (oneKeyPair != null) {\n" +
				"            return oneKeyPair;\n" +
				"        }\n" +
				"\n" +
				"        try {\n" +
				"            keyPairGen.initialize(KEY_SIZE, new SecureRandom(DateFormatUtils.format(new Date(), \"yyyyMMdd\").getBytes()));\n" +
				"            oneKeyPair = keyPairGen.generateKeyPair();\n" +
				"            return oneKeyPair;\n" +
				"        } catch (InvalidParameterException ex) {\n" +
				"            throw new IllegalStateException(\"KeyPairGenerator does not support a key length of \" + KEY_SIZE + \".\", ex);\n" +
				"        } catch (NullPointerException ex) {\n" +
				"            throw new IllegalStateException(\"RSAUtils#KEY_PAIR_GEN is null, can not generate KeyPairGenerator instance.\",\n" +
				"                    ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 返回已经生成的RSA密钥对。\n" +
				"     */\n" +
				"    private static KeyPair getKeyPair() {\n" +
				"        if (oneKeyPair == null) {\n" +
				"            return generateKeyPair();\n" +
				"        }\n" +
				"        return oneKeyPair;\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 根据给定的系数和专用指数构造一个RSA专用的公钥对象。\n" +
				"     *\n" +
				"     * @param modulus        系数。\n" +
				"     * @param publicExponent 专用指数。\n" +
				"     * @return RSA专用公钥对象。\n" +
				"     */\n" +
				"    public static RSAPublicKey generateRSAPublicKey(byte[] modulus, byte[] publicExponent) {\n" +
				"        RSAPublicKeySpec publicKeySpec = new RSAPublicKeySpec(new BigInteger(modulus),\n" +
				"                new BigInteger(publicExponent));\n" +
				"        try {\n" +
				"            return (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);\n" +
				"        } catch (InvalidKeySpecException ex) {\n" +
				"            throw new IllegalStateException(\"RSAPublicKeySpec is unavailable.\", ex);\n" +
				"        } catch (NullPointerException ex) {\n" +
				"            throw new IllegalStateException(\"RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.\", ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 根据给定的系数和专用指数构造一个RSA专用的私钥对象。\n" +
				"     *\n" +
				"     * @param modulus         系数。\n" +
				"     * @param privateExponent 专用指数。\n" +
				"     * @return RSA专用私钥对象。\n" +
				"     */\n" +
				"    public static RSAPrivateKey generateRSAPrivateKey(byte[] modulus, byte[] privateExponent) {\n" +
				"        RSAPrivateKeySpec privateKeySpec = new RSAPrivateKeySpec(new BigInteger(modulus),\n" +
				"                new BigInteger(privateExponent));\n" +
				"        try {\n" +
				"            return (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);\n" +
				"        } catch (InvalidKeySpecException ex) {\n" +
				"            throw new IllegalStateException(\"RSAPrivateKeySpec is unavailable.\", ex);\n" +
				"        } catch (NullPointerException ex) {\n" +
				"            throw new IllegalStateException(\"RSAUtils#KEY_FACTORY is null, can not generate KeyFactory instance.\", ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的私钥对象。\n" +
				"     *\n" +
				"     * @param hexModulus         系数。\n" +
				"     * @param hexPrivateExponent 专用指数。\n" +
				"     * @return RSA专用私钥对象。\n" +
				"     */\n" +
				"    public static RSAPrivateKey getRSAPrivateKey(String hexModulus, String hexPrivateExponent) {\n" +
				"        if (StringUtils.isEmpty(hexModulus) || StringUtils.isEmpty(hexPrivateExponent)) {\n" +
				"            throw new IllegalArgumentException(\"hexModulus and hexPrivateExponent cannot be empty. RSAPrivateKey value is null to return.\");\n" +
				"        }\n" +
				"        try {\n" +
				"            byte[] modulus = Hex.decodeHex(hexModulus.toCharArray());\n" +
				"            byte[] privateExponent = Hex.decodeHex(hexPrivateExponent.toCharArray());\n" +
				"            return generateRSAPrivateKey(modulus, privateExponent);\n" +
				"        } catch (DecoderException ex) {\n" +
				"            throw new IllegalStateException(\"hexModulus or hexPrivateExponent value is invalid.\", ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 根据给定的16进制系数和专用指数字符串构造一个RSA专用的公钥对象。\n" +
				"     *\n" +
				"     * @param hexModulus        系数。\n" +
				"     * @param hexPublicExponent 专用指数。\n" +
				"     * @return RSA专用公钥对象。\n" +
				"     */\n" +
				"    public static RSAPublicKey getRSAPublicKey(String hexModulus, String hexPublicExponent) {\n" +
				"        if (StringUtils.isEmpty(hexModulus) || StringUtils.isEmpty(hexPublicExponent)) {\n" +
				"            throw new IllegalArgumentException(\"hexModulus and hexPublicExponent cannot be empty. return null(RSAPublicKey).\");\n" +
				"        }\n" +
				"        try {\n" +
				"            byte[] modulus = Hex.decodeHex(hexModulus.toCharArray());\n" +
				"            byte[] publicExponent = Hex.decodeHex(hexPublicExponent.toCharArray());\n" +
				"            return generateRSAPublicKey(modulus, publicExponent);\n" +
				"        } catch (DecoderException ex) {\n" +
				"            throw new IllegalStateException(\"hexModulus or hexPublicExponent value is invalid.\", ex);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用指定的公钥加密数据。\n" +
				"     *\n" +
				"     * @param publicKey 给定的公钥。\n" +
				"     * @param data      要加密的数据。\n" +
				"     * @return 加密后的数据。\n" +
				"     */\n" +
				"    public static byte[] encrypt(PublicKey publicKey, byte[] data) {\n" +
				"        try {\n" +
				"            Cipher ci = Cipher.getInstance(ALGORITHM);\n" +
				"            ci.init(Cipher.ENCRYPT_MODE, publicKey);\n" +
				"            return ci.doFinal(data);\n" +
				"        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {\n" +
				"            throw new IllegalStateException(e);\n" +
				"        } catch (IllegalBlockSizeException e) {\n" +
				"            throw new IllegalArgumentException(\"can not encrypt data.\", e);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用指定的私钥解密数据。\n" +
				"     *\n" +
				"     * @param privateKey 给定的私钥。\n" +
				"     * @param data       要解密的数据。\n" +
				"     * @return 原数据。\n" +
				"     */\n" +
				"    public static byte[] decrypt(PrivateKey privateKey, byte[] data) {\n" +
				"        try {\n" +
				"            Cipher ci = Cipher.getInstance(ALGORITHM);\n" +
				"            ci.init(Cipher.DECRYPT_MODE, privateKey);\n" +
				"            return ci.doFinal(data);\n" +
				"        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | BadPaddingException e) {\n" +
				"            throw new IllegalStateException(e);\n" +
				"        } catch (IllegalBlockSizeException e) {\n" +
				"            throw new IllegalArgumentException(\"input data is not a validate RSA encrypted data\", e);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用给定的公钥加密给定的字符串。\n" +
				"     *\n" +
				"     * @param publicKey 给定的公钥。\n" +
				"     * @param plaintext 字符串。\n" +
				"     * @return 给定字符串的密文。\n" +
				"     */\n" +
				"    public static String encryptString(PublicKey publicKey, String plaintext) {\n" +
				"        if (Objects.isNull(publicKey) || Objects.isNull(plaintext)) {\n" +
				"            throw new IllegalArgumentException(\"public key or plain text should not be null\");\n" +
				"        }\n" +
				"        byte[] data = plaintext.getBytes();\n" +
				"        byte[] encryptData = encrypt(publicKey, data);\n" +
				"        return new String(Hex.encodeHex(encryptData));\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用默认的公钥加密给定的字符串。\n" +
				"     * <p/>\n" +
				"     * 若{@code plaintext} 为 {@code null} 则返回 {@code null}。\n" +
				"     *\n" +
				"     * @param plaintext 字符串。\n" +
				"     * @return 给定字符串的密文。\n" +
				"     */\n" +
				"    public static String encryptString(String plaintext) {\n" +
				"        return encryptString(getDefaultPublicKey(), plaintext);\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用给定的私钥解密给定的字符串。\n" +
				"     * <p/>\n" +
				"     * 若私钥为 {@code null}，或者 {@code encrypttext} 为 {@code null}或空字符串则返回 {@code null}。\n" +
				"     * 私钥不匹配时，返回 {@code null}。\n" +
				"     *\n" +
				"     * @param privateKey    给定的私钥。\n" +
				"     * @param encryptedText 密文。\n" +
				"     * @return 原文字符串。\n" +
				"     */\n" +
				"    public static String decryptString(PrivateKey privateKey, String encryptedText) {\n" +
				"        if (privateKey == null || encryptedText == null) {\n" +
				"            throw new IllegalArgumentException(\"private key or encrypted text should not be null!\");\n" +
				"        }\n" +
				"        try {\n" +
				"            byte[] encryptedData = Hex.decodeHex(encryptedText.toCharArray());\n" +
				"            byte[] data = decrypt(privateKey, encryptedData);\n" +
				"            return new String(data, StandardCharsets.UTF_8);\n" +
				"        } catch (DecoderException e) {\n" +
				"            throw new IllegalArgumentException(\"encrypted text is not a legal hex string\", e);\n" +
				"        }\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 使用默认的私钥解密给定的字符串。\n" +
				"     *\n" +
				"     * @param encryptedText 密文。\n" +
				"     * @return 原文字符串。\n" +
				"     */\n" +
				"    public static String decryptString(String encryptedText) {\n" +
				"        return decryptString(getDefaultPrivateKey(), encryptedText);\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 返回已初始化的默认的公钥。\n" +
				"     */\n" +
				"    public static RSAPublicKey getDefaultPublicKey() {\n" +
				"        KeyPair keyPair = getKeyPair();\n" +
				"        return (RSAPublicKey) keyPair.getPublic();\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 返回已初始化的默认的私钥。\n" +
				"     */\n" +
				"    public static RSAPrivateKey getDefaultPrivateKey() {\n" +
				"        KeyPair keyPair = getKeyPair();\n" +
				"        return (RSAPrivateKey) keyPair.getPrivate();\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 返回当前默认公钥的模数和指数信息\n" +
				"     *\n" +
				"     * @return 包含系数和模的不可变map，{@link RSAUtils#MODULUS_NAME}, {@link RSAUtils#EXPONENT_NAME}\n" +
				"     */\n" +
				"    public static Map<String, String> publicKeyInfo() {\n" +
				"        return publicKeyInfo(getDefaultPublicKey());\n" +
				"    }\n" +
				"\n" +
				"    /**\n" +
				"     * 将rsa公钥使用的模和系数经过16进制转换后放入map并返回。\n" +
				"     *\n" +
				"     * @return 包含系数和模的不可变map，{@link RSAUtils#MODULUS_NAME}, {@link RSAUtils#EXPONENT_NAME}\n" +
				"     */\n" +
				"    public static Map<String, String> publicKeyInfo(RSAPublicKey publicKey) {\n" +
				"        return ImmutableMap.<String, String>builder()\n" +
				"                .put(RSAUtils.MODULUS_NAME, new String(Hex.encodeHex(publicKey.getModulus().toByteArray())))\n" +
				"                .put(RSAUtils.EXPONENT_NAME, new String(Hex.encodeHex(publicKey.getPublicExponent().toByteArray())))\n" +
				"                .build();\n" +
				"    }\n" +
				"}\n";
	}
}
