package jp.co.hottolink.buzzfund.crawler.parser;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jp.co.hottolink.buzzfund.common.parser.HTMLParser;
import jp.co.hottolink.fusion.core.util.net.RssUtil;
import jp.co.hottolink.fusion.core.util.net.feed.FeedChannel;
import jp.co.hottolink.fusion.core.util.net.feed.FeedItem;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.TagNameFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * <p>
 * RssParserのテストクラス.
 * </p>
 * @author higa
 */
public class RssParserTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			checkBlogUrls();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * ブログURLをチェックする.
	 * </p>
	 * @param blogUrls ブログURL
	 * @throws ParserException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void checkBlogUrls(String[] blogUrls) throws ParserException, IOException, SQLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			for (String blogUrl: blogUrls) {
				executor.preparedStatement("select rss_url from rss_url where blog_url = ?");
				executor.setString(1, blogUrl);
				executor.executeQuery();

				String rssUrl = null;
				if (executor.next()) rssUrl = executor.getString("rss_url");
				if (rssUrl == null) rssUrl = RssParser.getRssUrl(blogUrl);

				try {
					RssParser.getRss(rssUrl);
					System.out.println(blogUrl + "\t" + "success");
				} catch (Exception e) {
					System.out.println(blogUrl + "\t" + "failed" + "\t" + e);
				}
			}
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * RSSのURLをチェックする.
	 * </p>
	 * @param rssUrls RSSのURL
	 */
	public static void checkRssUrls(String[] rssUrls) {
		for (String rssUrl: rssUrls) {
			checkRssUrl(rssUrl);
		}
	}

	/**
	 * <p>
	 * RSSのURLをチェックする.
	 * </p>
	 * @param rssUrl RSSのURL
	 */
	public static void checkRssUrl(String rssUrl) {

		String text = null;
		try {
			text = RssParser.getRssText(rssUrl);
		} catch (Exception e) {
			System.out.println(rssUrl + "\t" + "connect failed" + "\t" + e);
			return;
		}

		// フィードの取得
		FeedChannel channel = null;
		try {
			channel = RssUtil.getRssFromText(text, false);
			//channel = RssUtil.getRss(rssUrl, false);
		} catch (Exception e) {
			System.out.println(rssUrl + "\t" + "feed failed" + "\t" + e);
			//e.printStackTrace();
			return;
		}

		// 記事数のチェック
		List<FeedItem> items = channel.getItems();
		if (items.isEmpty()) {
			System.out.println(rssUrl + "\t" + "記事なし");
			return;
		}

		// 最新ブログ日時の取得
		Date pubDate = items.get(0).getPubDate();
		if (pubDate == null) {
			System.out.println(rssUrl + "\t" + "日付なし");
		} else {
			String latest = DateUtil.toString(pubDate, "yyyy年MM月dd日");
			System.out.println(rssUrl + "\t" + latest);
		}
	}

	/**
	 * <p>
	 * URLからHTMLヘッダのRSSのlink要素を取得する.
	 * </p>
	 * @param url URL
	 * @return RSSのlink要素
	 * @throws ParserException
	 * @throws IOException 
	 */
	public static List<TagNode> getRssLinkTags(String url) throws ParserException, IOException {

		// パーサーの取得
		HTMLParser parser = HTMLParser.createParser(url);

		// HTMLヘッダの<link rel="alternate" />
		NodeFilter[] filters = new NodeFilter[6];
		filters[0] = new HasParentFilter(new TagNameFilter("head"));
		filters[1] = new TagNameFilter("link");
		filters[2] = new HasAttributeFilter("rel", "alternate");
		filters[3] = new OrFilter(new HasAttributeFilter("type", "application/rss+xml")
							, new HasAttributeFilter("type", "application/atom+xml"));
		filters[4] = new HasAttributeFilter("href");
		filters[5] = new NotFilter(new HasAttributeFilter("title", "ROR"));
		NodeFilter filter = new AndFilter(filters);

		// link要素の取得
		NodeList nodes = parser.parse(filter);

		List<TagNode> tags = new ArrayList<TagNode>();
		for (Node node: nodes.toNodeArray()) {
			tags.add((TagNode)node);
		}

		return tags;
	}

	/**
	 * <p>
	 * ブログURLをチェックする.
	 * </p>
	 * @throws ParserException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void checkBlogUrls() throws ParserException, IOException, SQLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			executor.executeQuery("select * from crawl_result where status = 3 or status = 1");

			while (executor.next()) {
				String blogUrl = executor.getString("blog_url");
				String rssUrl = executor.getString("rss_url");

				if (rssUrl == null) {
					try {
						rssUrl = RssParser.getRssUrl(blogUrl);
						if (rssUrl == null) System.out.println(blogUrl + "\t" + "rss url not found");
					} catch (Exception e) {
						System.out.println(blogUrl + "\t" + "connect failed" + "\t" + e);
						continue;
					}
				}

				// フィードの取得
				FeedChannel channel = null;
				try {
					channel = RssParser.getRss(rssUrl);
				} catch (Exception e) {
					System.out.println(blogUrl + "\t" + rssUrl + "\t" + "feed failed" + "\t" + e);
					continue;
				}

				// 記事数のチェック
				List<FeedItem> items = channel.getItems();
				if (items.isEmpty()) {
					System.out.println(blogUrl + "\t" + rssUrl + "\t" + "記事なし");
					continue;
				}

				// 最新ブログ日時の取得
				Date pubDate = items.get(0).getPubDate();
				if (pubDate == null) {
					System.out.println(blogUrl + "\t" + rssUrl + "\t" + "日付なし");
				} else {
					String latest = DateUtil.toString(pubDate, "yyyy年MM月dd日");
					System.out.println(blogUrl + "\t" + rssUrl + "\t" + latest);
				}
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}
}
