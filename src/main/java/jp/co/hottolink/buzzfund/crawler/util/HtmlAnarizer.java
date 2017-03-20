package jp.co.hottolink.buzzfund.crawler.util;

import org.htmlparser.Parser;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * <p>
 * HTMLを解析するクラス.
 * </p>
 * @author higa
 */
public class HtmlAnarizer {

	/**
	 * <p>
	 * デフォルトエンコーディング.
	 * </p>
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * <p>
	 * htmlテキストをプレーンテキストにする.
	 * </p>
	 * @param html テキスト
	 * @return プレーンテキスト
	 */
	public static String toPlaneText(String html) throws ParserException {
	
		if (html == null) {
			return null;
		}
	
		html = html.replaceAll("<[Bb][Rr][ ]*/?>", "\n");
		Parser parser = Parser.createParser(html, DEFAULT_ENCODING);
		NodeList nodes = parser.parse(null);
		String text = nodes.asString();
	
		text = text.replaceAll("&nbsp;", " ");
		text = text.replaceAll("&lt;", "<");
		text = text.replaceAll("&gt;", ">");
		text = text.replaceAll("&quot;", "\"");

		return text;
	}
}
