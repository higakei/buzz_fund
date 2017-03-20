package jp.co.hottolink.buzzfund.crawler.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Tag;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.tags.ProcessingInstructionTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jp.co.hottolink.buzzfund.common.parser.HTMLParser;
import jp.co.hottolink.fusion.core.util.net.RssUtil;
import jp.co.hottolink.fusion.core.util.net.feed.FeedChannel;

/**
 * <p>
 * RSSのHTMLパーサークラス.
 * </p>
 * @author higa
 */
public class RssParser {

	/**
	 * <p>
	 * 制御コード.
	 * </p>
	 */
	private static final char[] CONTROL_CODES = {
		0x13, 0x15, 0x11, 0x01, 0x0f, 0x0b, 0x1b
	};

	/**
	 * <p>
	 * ページのRSSのURLを取得する.
	 * </p>
	 * @param pageUrl ページのURL
	 * @return RSSのURL
	 * @throws ParserException
	 * @throws IOException
	 */
	public static String getRssUrl(String pageUrl) throws ParserException,
			IOException {
		List<String> rssUrls = getRssUrls(pageUrl);
		if (rssUrls.isEmpty()) {
			return null;
		} else {
			return rssUrls.get(0);
		}
	}

	/**
	 * <p>
	 * ページのRSSのURLを取得する.
	 * </p>
	 * @param pageUrl ページのURL
	 * @return RSSのURL
	 * @throws ParserException
	 * @throws IOException
	 */
	public static List<String> getRssUrls(String pageUrl)
			throws ParserException, IOException {

		// パーサーの取得
		HTMLParser parser = HTMLParser.createParser(pageUrl);

		// パース
		NodeFilter[] filters = new NodeFilter[6];
		filters[0] = new HasParentFilter(new TagNameFilter("head"));
		filters[1] = new TagNameFilter("link");
		filters[2] = new HasAttributeFilter("rel", "alternate");
		filters[3] = new OrFilter(new HasAttributeFilter("type", "application/rss+xml")
							, new HasAttributeFilter("type", "application/atom+xml"));
		filters[4] = new HasAttributeFilter("href");
		filters[5] = new NotFilter(new HasAttributeFilter("title", "ROR"));
		NodeFilter filter = new AndFilter(filters);
		NodeList nodes = parser.parse(filter);

		List<String> rssUrls = new ArrayList<String>();
		if (nodes.size() == 0) {
			return rssUrls;
		}

		// RSSのURLの取得
		for (Node node: nodes.toNodeArray()) {
			if (!(node instanceof Tag)) continue;
			String href = ((Tag)node).getAttribute("href");
			rssUrls.add(href);
		}

		return rssUrls;
	}

	/**
	 * <p>
	 * RSSのテキストを取得する.
	 * </p>
	 * @param rssUrl RSSのURL
	 * @return RSSのテキスト
	 * @throws ParserException
	 * @throws IOException
	 * @throws XMLStreamException 
	 */
	public static String getRssText(String rssUrl) throws ParserException,
			IOException, XMLStreamException {

		// XMLタグの取得
		HTMLParser parser = HTMLParser.createParser(rssUrl);
		NodeFilter filter = new NodeClassFilter(ProcessingInstructionTag.class);
		NodeList nodes = parser.parse(filter);
		if (nodes.size() == 0) {
			throw new RuntimeException(rssUrl + " is no xml");
		}

		// XMLのエンコードの取得
		ProcessingInstructionTag tag = (ProcessingInstructionTag)nodes.elementAt(0);
		String encoding = getEncoding(tag);
		if (encoding == null) {
			encoding = "UTF-8";
		}

		// RSSのテキストの取得
		parser = HTMLParser.createParser(rssUrl);
		String text = parser.getSource(encoding);

		// BOMの削除
		if ((text != null) && !text.isEmpty() && (text.charAt(0) == 0xFEFF)) {
			text = text.substring(1);
		}

		// 制御コードの削除
		text = removeCotrolCode(text);

		return text;
	}

	/**
	 * <p>
	 * RSSフィードを取得する.
	 * </p>
	 * @param rssUrl RSSのURL
	 * @return RSSフィード
	 * @throws ParserException
	 * @throws IOException
	 * @throws XMLStreamException 
	 */
	public static FeedChannel getRss(String rssUrl) throws ParserException,
			IOException, XMLStreamException {
		try {
			return RssUtil.getRss(rssUrl, false);
		} catch (Exception e) {
			String text = getRssText(rssUrl);
			return RssUtil.getRssFromText(text, false);
		}
	}

	/**
	 * <p>
	 * 文字列から制御コードを削除する.
	 * </p>
	 * @param string 削除する文字列
	 * @return 削除した文字列
	 */
	private static String removeCotrolCode(String string) {

		if (string == null) {
			return null;
		}

		for (char code: CONTROL_CODES) {
			string = string.replaceAll(String.valueOf(code), "");
		}

		return string;
	}

	/**
	 * <p>
	 * XML宣言タグからエンコーディングを取得する.
	 * </p>
	 * @param tag XML宣言タグ
	 * @return エンコーディング
	 * @throws XMLStreamException 
	 */
	private static String getEncoding(ProcessingInstructionTag tag)
			throws XMLStreamException {

		if (tag == null) {
			return null;
		}

		String html = tag.toHtml();
		if (html == null) {
			return null;
		}

		ByteArrayInputStream stream = new ByteArrayInputStream(html.getBytes());
		XMLInputFactory factory = XMLInputFactory.newInstance();
		XMLStreamReader reader = factory.createXMLStreamReader(stream);

		return reader.getEncoding();
	}
}
