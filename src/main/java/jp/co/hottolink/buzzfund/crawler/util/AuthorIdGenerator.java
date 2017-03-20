package jp.co.hottolink.buzzfund.crawler.util;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;

import jp.co.hottolink.splogfilter.common.api.buzz.util.BuzzAuthorIdGenerator;

/**
 * <p>
 * 著者IDを生成するクラス.
 * </p>
 * @author higa
 */
public class AuthorIdGenerator {

	/**
	 * <p>
	 * URLから著者IDを生成する.
	 * </p>
	 * @param urlString URL
	 * @return 著者ID
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException 
	 * @throws URISyntaxException 
	 */
	public static String generate(String urlString)
			throws NoSuchAlgorithmException, MalformedURLException, URISyntaxException {

		if (urlString == null) {
			return null;
		}

		// buzzのauthorIdは、そのまま利用する
		String authorId = BuzzAuthorIdGenerator.generate(urlString);
		if (authorId != null) {
			return authorId;
		}

		URL url = new URL(urlString);
		String host = url.getHost();
		String digest = MessageDigestUtil.digest("MD5", urlString);

		return host + ":" + digest;
	}
}
