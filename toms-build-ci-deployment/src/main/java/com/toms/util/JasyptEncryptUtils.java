package com.toms.util;

import java.security.MessageDigest;

import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.util.password.ConfigurablePasswordEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;
/**
 * 암호화 모듈
 * [사용법]
 *   복호화가 필요한 암호화 JasyptEncryptUtils.encrypt("메시지");
 *   복호화가 되면 않되는 암호화는 JasyptEncryptUtils.encryptPassword("메시지");
 * @author airlee
 *
 */
public class JasyptEncryptUtils {
	// 암호화 seed 문자
	private static final String SEED_KEY = "thinkonemoreseconds";

	private static String algorithm ="SHA-256";
	
	@SuppressWarnings("static-access")
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	/**
	 * 암호화
	 * 
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public static String encrypt(String msg)  {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(SEED_KEY);
		String encryptedPassword = textEncryptor.encrypt(msg);
		byte[] encryptedByte = Base64.encodeBase64(encryptedPassword.getBytes());
		return new String(encryptedByte);
	}

	/**
	 * 복호화
	 * 
	 * @param encryptedString
	 * @return
	 * @throws Exception
	 */
	public static String decrypt(String encryptedString)  {
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(SEED_KEY);
		byte[] encryptedByte = Base64.decodeBase64(encryptedString.getBytes());
		String decryptedStr = textEncryptor.decrypt(new String(encryptedByte));
		return decryptedStr;
	}

	/**
	 * 비밀번호 암호화 - 복호화가 되지 않는 암호화
	 * 
	 * @param msg
	 *            암호화하려는 문자열
	 * @return String 암호화된 문자열
	 */
	public static String encryptPassword(String msg) throws Exception {
		return getHashValue("MD5", msg);
	}
	
	/**
	 * 비밀번호 암호화 2- jasypt복호화가 되지 않는 암호화
	 * 
	 * @param msg
	 *            암호화하려는 문자열
	 * @return String 암호화된 문자열
	 */
	public static String encryptPassword2(String msg) throws Exception {
		ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        passwordEncryptor.setAlgorithm(algorithm);
       // passwordEncryptor.setPlainDigest(true);
        return  passwordEncryptor.encryptPassword(msg);
	}
	
	/**
	 * 비밀번호 체크 - jasypt복호화가 되지 않는 암호화
	 * 
	 * @param plainText
	 *            평문 문자열
	 * @param encryptedText 암호화된 문자열
	 * @return String 암호화된 문자열
	 */
	public static boolean checkPassword2(String plainText,String encryptedText) throws Exception {
		ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
		passwordEncryptor.setAlgorithm(algorithm);
		// passwordEncryptor.setPlainDigest(true);
		return  passwordEncryptor.checkPassword(plainText, encryptedText);
	}

	/**
	 * MD5 Hash 수행
	 * 
	 * @param type
	 *            MD5 타입
	 * @param msg
	 *            암호화하려는 문자열
	 * @return String 암호화된 문자열
	 */
	private static String getHashValue(String type, String msg)
			throws Exception {

		MessageDigest digest = MessageDigest.getInstance(type);

		digest.update(msg.getBytes());
		byte[] byteHash = digest.digest();

		byte[] strHash = Base64.encodeBase64(byteHash);

		return new String(strHash);

	}

	public static void main(String args[]) throws Exception {
		String myText = "aaaa11!!";
//		String bb = JasyptEncryptUtils.encrypt(myText);
//		System.out.println(bb);
//		System.out.println(JasyptEncryptUtils.decrypt(bb));
//		System.out.println(JasyptEncryptUtils.encryptPassword(myText));
		String encrypted = JasyptEncryptUtils.encryptPassword2(myText);
		System.out.println(encrypted+","+JasyptEncryptUtils.checkPassword2(myText, encrypted));


	}
}
