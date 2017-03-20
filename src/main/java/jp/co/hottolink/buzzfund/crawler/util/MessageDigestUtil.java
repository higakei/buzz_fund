package jp.co.hottolink.buzzfund.crawler.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * メッセージダイジェストのUtilクラス.
 * </p>
 * @author higa
 */
public class MessageDigestUtil {

	/**
	 * <p>
	 * 文字列のメッセージダイジェストを生成する.
	 * </p>
	 * @param algorithm アルゴリズム
	 * @param input 文字列
	 * @return メッセージダイジェスト
	 * @throws NoSuchAlgorithmException
	 */
	public static String digest(String algorithm, String input) throws NoSuchAlgorithmException {
	
		// ダイジェストを生成
		MessageDigest md = MessageDigest.getInstance(algorithm);
		md.update(input.getBytes());
		byte[] digest = md.digest();
	
		// ダイジェストを16進数文字列に変換
		StringBuffer buffer = new StringBuffer();
		for (byte b: digest) {
			buffer.append(String.format("%02x", b));
		}
	
		return buffer.toString();
	}
}
