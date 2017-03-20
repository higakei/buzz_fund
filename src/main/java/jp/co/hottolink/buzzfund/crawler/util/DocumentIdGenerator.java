package jp.co.hottolink.buzzfund.crawler.util;

import java.net.MalformedURLException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * 文書IDを生成するクラス.
 * </p>
 * @author higa
 */
public class DocumentIdGenerator {

	/**
	 * <p>
	 * 著者IDとURLから文書IDを生成する.
	 * </p>
	 * @param authorId 著者ID
	 * @param url 文書ID
	 * @return 文書ID
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException
	 */
	public static String generate(String authorId, String url)
			throws NoSuchAlgorithmException, MalformedURLException {

		if ((authorId == null) || (url == null)) {
			return null;
		}

		String digest = MessageDigestUtil.digest("MD5", url);
		return authorId + ":" + digest;
	}
}
