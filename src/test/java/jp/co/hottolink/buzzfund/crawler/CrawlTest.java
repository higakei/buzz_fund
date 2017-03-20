package jp.co.hottolink.buzzfund.crawler;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlparser.util.ParserException;

import jp.co.hottolink.buzzfund.common.parser.HTMLParser;
import jp.co.hottolink.buzzfund.crawler.model.ToshiBlogsModel;
import jp.co.hottolink.buzzfund.crawler.parser.RssParser;
import jp.co.hottolink.buzzfund.crawler.util.AuthorIdGenerator;
import jp.co.hottolink.fusion.core.util.net.feed.FeedChannel;
import jp.co.hottolink.fusion.core.util.net.feed.FeedItem;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * クロールのテストクラス.
 * </p>
 * @author higa
 */
public class CrawlTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			checkUrl();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>
	 * 著者IDの重複チェックを行う.
	 * </p>
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public static void checkAuthorId() 
			throws SQLException, NoSuchAlgorithmException, MalformedURLException, URISyntaxException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			executor.executeQuery("select * from crawl_result");

			Map<String, List<String>> map = new HashMap<String, List<String>>();
			while (executor.next()) {
				String blogUrl = executor.getString("blog_url");
				String authorId = AuthorIdGenerator.generate(blogUrl);
				List<String> list = map.get(authorId);
				if (list == null) list = new ArrayList<String>();
				list.add(blogUrl);
				map.put(authorId, list);
			}

			for (String authorId: map.keySet()) {
				List<String> list = map.get(authorId);
				if (list.size() > 1) {
					System.out.println(authorId + "\t" + list);
				}
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * URLの重複チェックを行う.
	 * </p>
	 * @throws SQLException 
	 * @throws MalformedURLException 
	 * @throws NoSuchAlgorithmException 
	 */
	public static void checkUrl() throws SQLException, NoSuchAlgorithmException, MalformedURLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			executor.executeQuery("select url, count(*) as count from blogfeedRDF group by binary url having count > 1");
			List<String> urls = new ArrayList<String>();
			while (executor.next()) {
				urls.add(executor.getString("url"));
			}

			List<List<String>> list = new ArrayList<List<String>>();
			loop: for (String url: urls) {
				executor.preparedStatement("select authorId from blogfeedRDF where binary url = ?");
				executor.setString(1, url);
				executor.executeQuery();

				List<String> ids = new ArrayList<String>();
				while (executor.next()) {
					String authorId = executor.getString("authorId");
					ids.add(authorId);
				}

				for (List<String> element: list) {
					if (element.containsAll(ids)) {
						continue loop;
					}
				}

				list.add(ids);
			}

			for (List<String> element: list) {
				System.out.println(element);
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * ブログURLをチェックする.
	 * </p>
	 * @throws SQLException
	 */
	public static void checkBlogUrl() throws SQLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			executor.executeQuery("select * from crawl_result where status = 4");

			while (executor.next()) {
				String blogUrl = executor.getString("blog_url");
				try {
					HttpURLConnection connection = (HttpURLConnection)HTMLParser.getConnection(blogUrl);
					int responseCode = connection.getResponseCode();
					System.out.println(blogUrl + "\t" + responseCode);
				} catch (Exception e) {
					System.out.println(blogUrl + "\t" + e);
				}
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * ブログURLをチェックする.
	 * </p>
	 * @param blogUrls ブログURL
	 * @throws IOException
	 */
	public static void checkBlogUrls(String[] blogUrls) throws IOException {
		for (String blogUrl: blogUrls) {
			checkBlogUrl(blogUrl);
		}
	}

	/**
	 * <p>
	 * ブログURLをチェックする.
	 * </p>
	 * @param blogUrl ブログURL
	 */
	public static void checkBlogUrl(String blogUrl) {

		// ブログURLの接続チェック
		try {
			HttpURLConnection connection = (HttpURLConnection)HTMLParser.getConnection(blogUrl);
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println(blogUrl + "\t" + "-" + "\t" + "page" + "\t" + responseCode);
				return;
			}
		} catch (ParserException e) {
			Throwable throwable = e.getThrowable();
			if (throwable instanceof UnknownHostException) {
				System.out.println(blogUrl + "\t" + "-" + "\t" + "page" + "\t" + "ホストなし");
			} else {
				System.out.println(blogUrl + "\t" + "-" + "\t" + "page connect failed" + "\t" + throwable);
			}
			return;
		} catch (Exception e) {
			System.out.println(blogUrl + "\t" + "-" + "\t" + "page connect failed" + "\t" + e);
			return;
		}

		// RSSのURLの取得
		String rssUrl = null;
		try {
			rssUrl = getRssUrlFromDB(blogUrl);
			if (rssUrl == null) rssUrl = getRssUrl(blogUrl);
			if (rssUrl == null) {
				System.out.println(blogUrl + "\t" + "-" + "\t" + "rss url not found");
				return;
			}
		} catch (Exception e) {
			System.out.println(blogUrl + "\t" + "-" + "\t" + "page parse failed" + "\t" + e);
			return;
		}

		// RSSのURLの接続チェック
		try {
			HttpURLConnection connection = (HttpURLConnection)HTMLParser.getConnection(rssUrl);
			int responseCode = connection.getResponseCode();
			if (responseCode != HttpURLConnection.HTTP_OK) {
				System.out.println(blogUrl + "\t" + rssUrl + "\t" + "rss" + "\t" + responseCode);
				return;
			}
		} catch (ParserException e) {
			Throwable throwable = e.getThrowable();
			if (throwable instanceof UnknownHostException) {
				System.out.println(blogUrl + "\t" + rssUrl + "\t" + "rss" + "\t" + "ホストなし");
			} else {
				System.out.println(blogUrl + "\t" + rssUrl + "\t" + "rss connect failed" + "\t" + throwable);
			}
			return;
		} catch (Exception e) {
			System.out.println(blogUrl + "\t" + rssUrl + "\t" + "rss connect failed" + "\t" + e);
			return;
		}

		// フィードの取得
		FeedChannel channel = null;
		try {
			channel = RssParser.getRss(rssUrl);
		} catch (Exception e) {
			System.out.println(blogUrl + "\t" + rssUrl + "\t" + "rss parse failed" + "\t" + e);
			return;
		}

		// 記事数のチェック
		List<FeedItem> items = channel.getItems();
		if (items.isEmpty()) {
			System.out.println(blogUrl + "\t" + rssUrl + "\t" + "記事なし");
			return;
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

	/**
	 * <p>
	 * フィードしたURLをチェックする.
	 * </p>
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	public static void checkFeedUrl() throws SQLException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException {

		SQLExecutor executor = null;

		try {
			// DBに接続
			executor = new SQLExecutor("buzz_fund_db");

			// ブログURLの取得
			List<String> blogUrls = new ArrayList<String>();
			executor.executeQuery("select blog_url from crawl_result");
			while (executor.next()) {
				blogUrls.add(executor.getString("blog_url"));
			}

			// フィードしたURLのチェック
			for (String blogUrl: blogUrls) {
				String authorId = AuthorIdGenerator.generate(blogUrl);
				executor.preparedStatement("select url from blogfeedRDF where authorId = ?");
				executor.setString(1, authorId);
				executor.executeQuery();

				while (executor.next()) {
					String url = executor.getString("url");
					if (!url.startsWith(blogUrl)) {
						System.out.println(blogUrl + "\t" + authorId + "\t" + url);
					}
				}
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * DBからRSSのURLを取得する.
	 * </p>
	 * @param blogUrl ブログURL
	 * @return RSSのURL
	 * @throws SQLException
	 */
	private static String getRssUrlFromDB(String blogUrl) throws SQLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			executor.preparedStatement("select rss_url from rss_url where blog_url = ?");
			executor.setString(1, blogUrl);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			return executor.getString("rss_url");

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * RSSのURLを取得する.
	 * </p>
	 * @param blogUrl ブログURL
	 * @return RSSのURL
	 * @throws ParserException
	 * @throws IOException 
	 */
	private static String getRssUrl(String blogUrl) throws ParserException, IOException {
		ToshiBlogsModel model = new ToshiBlogsModel();
		return model.getRssUrlFromHtml(blogUrl);
	}
}
