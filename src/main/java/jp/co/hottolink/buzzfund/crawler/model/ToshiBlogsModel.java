package jp.co.hottolink.buzzfund.crawler.model;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.htmlparser.util.ParserException;

import jp.co.hottolink.buzzfund.crawler.dao.BlogFeedDao;
import jp.co.hottolink.buzzfund.crawler.dao.CrawlResultDao;
import jp.co.hottolink.buzzfund.crawler.dao.ToshiBlogsDao;
import jp.co.hottolink.buzzfund.crawler.entity.BlogFeedEntity;
import jp.co.hottolink.buzzfund.crawler.entity.CrawlResultEntity;
import jp.co.hottolink.buzzfund.crawler.parser.RssParser;
import jp.co.hottolink.buzzfund.crawler.util.AuthorIdGenerator;
import jp.co.hottolink.buzzfund.crawler.util.DocumentIdGenerator;
import jp.co.hottolink.buzzfund.crawler.util.HtmlAnarizer;
import jp.co.hottolink.fusion.core.util.net.feed.FeedChannel;
import jp.co.hottolink.fusion.core.util.net.feed.FeedItem;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;

/**
 * <p>
 * 投資分析ブログのModelクラス.
 * </p>
 * @author higa
 */
public class ToshiBlogsModel {

	/**
	 * <p>
	 * DB接続プロパティファイル名.
	 * </p>
	 */
	private static final String DB_RESOURCE = "buzz_fund_db";

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private static Logger logger = Logger.getLogger(ToshiBlogsModel.class);

	/**
	 * <p>
	 * クロールするURLを取得する.
	 * </p>
	 * @return クロールするURL
	 */
	public List<String> getCrawlUrls() {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			ToshiBlogsDao dao = new ToshiBlogsDao(executor);
			return dao.getCrawlUrls(); 
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * ブログのURLからRSSのURLを取得する.
	 * </p>
	 * @param crawl クロール情報
	 * @return RSSのURL
	 */
	public String getRssUrl(CrawlResultEntity crawl) {

		// クロール結果テーブルから取得
		String rssUrl = crawl.getRssUrl();
		if (rssUrl != null) {
			crawl.setRssUrl(rssUrl);
			return rssUrl;
		}

		// HTMLから取得
		String blogUrl = crawl.getBlogUrl();
		try {
			rssUrl = getRssUrlFromHtml(blogUrl);
			if (rssUrl == null) {
				crawl.setStatus(CrawlResultEntity.STATUS_RSS_URL_NOT_FOUND);
			}
		} catch (ParserException e) {
			Throwable t = e.getThrowable();
			if ((t instanceof FileNotFoundException)
					|| (t instanceof UnknownHostException)) {
				crawl.setStatus(CrawlResultEntity.STATUS_PAGE_NOT_FOUND);
			} else {
				logger.warn(blogUrl, e);
				crawl.setStatus(CrawlResultEntity.STATUS_RSS_URL_FAILED);
			}
		} catch (Exception e) {
			logger.warn(blogUrl, e);
			crawl.setStatus(CrawlResultEntity.STATUS_RSS_URL_FAILED);
		}

		crawl.setRssUrl(rssUrl);
		return rssUrl;
	}

	/**
	 * <p>
	 * HTMLからRSSのURLを取得する.
	 * </p>
	 * @param htmlUrl HTMLのURL
	 * @return RSSのURL
	 * @throws ParserException
	 * @throws IOException 
	 */
	public String getRssUrlFromHtml(String htmlUrl) throws ParserException, IOException {

		// RSSのURLの取得
		String rssUrl = RssParser.getRssUrl(htmlUrl);
		if (rssUrl == null) {
			return null;
		}

		// 絶対パスの場合
		try {
			new URL(rssUrl);
			return rssUrl;
		} catch (MalformedURLException e) {}

		// HTMLパスの取得
		URL url = new URL(htmlUrl);
		String path = url.getPath();
		int index = path.lastIndexOf("/");
		String base = path.substring(0, index);

		// パスの作成
		if (rssUrl.startsWith("/")) {
			path = rssUrl;
		} else {
			path = base + "/" + rssUrl;
		}

		// URLの作成
		url = new URL(url.getProtocol(), url.getHost(), path);
		rssUrl = url.toString();

		return rssUrl;
	}

	/**
	 * <p>
	 * クロールする.
	 * </p>
	 * @param crawl クロール情報
	 * @param isLatest true:最新記事のみクロールする, false:通常のクロール
	 * @return クロール結果
	 */
	public CrawlResultEntity crawl(CrawlResultEntity crawl, boolean isLatest) {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			return crawl(executor, crawl, isLatest);
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * クロールする.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param crawl クロール情報
	 * @param isLatest true:最新記事のみクロールする, false:通常のクロール
	 * @return クロール結果
	 */
	public CrawlResultEntity crawl(SQLExecutor executor, CrawlResultEntity crawl, boolean isLatest) {

		FeedChannel channel = null;
		int insertCount = 0;

		String blogUrl = crawl.getBlogUrl();
		String rssUrl = crawl.getRssUrl();

		// RSSフィードの取得
		try {
			channel = RssParser.getRss(rssUrl);
		} catch (Exception e) {
			logger.warn(rssUrl, e);
		}

		// RSSフィードが取得に失敗した場合
		if (channel == null) {
			crawl.setRssUrl(null);
			crawl.setStatus(CrawlResultEntity.STATUS_FEED_FAILED);
			return crawl;
		}

		// フィード数の設定
		List<FeedItem> items = channel.getItems();
		crawl.setFeedCount(items.size());

		// フィードの保存
		for (FeedItem item: items) {

			// URLがない場合
			String link = item.getLink();
			if (link == null) {
				continue;
			}

			// クロール判定
			Date pubDate = item.getPubDate();
			boolean isCrawl = isCrawl(pubDate);
			if (!isCrawl) {
				continue;
			} else if (isLatest) {
				isCrawl = isCrawl(pubDate, crawl.getLatestBlogDate());
				if (!isCrawl) {
					continue;
				}
			}

			// Seesaaの広告ページ
			if (link.startsWith("http://www.seesaa.jp/r.pl")
					|| link.startsWith("http://match.seesaa.jp/ot_listing.pl")) {
				continue;
			}

			// ブログフィードの作成
			BlogFeedEntity blogFeed = null;
			try {
				blogFeed = getBlogFeed(blogUrl, item);
			} catch (Exception e) {
				logger.warn(item.getLink(), e);
				continue;
			}

			// 追加したフィード数のカウント
			boolean isInsert = setBlogFeed(executor, blogFeed);
			if (isInsert) {
				insertCount += 1;
			}

			// 最新ブログ日時の更新
			if (isInsert) {
				Timestamp blogDate = blogFeed.getDate();
				Timestamp latestBlogDate = crawl.getLatestBlogDate();
				if ((blogDate != null)
						&& ((latestBlogDate == null) || blogDate.after(latestBlogDate))) {
					crawl.setLatestBlogDate(blogDate);
				}
			}
		}

		// クロール結果の設定
		crawl.setInsertCount(insertCount);
		int totalCrawlCount = crawl.getTotalCrawlCount() + insertCount;
		crawl.setTotalCrawlCount(totalCrawlCount);
		crawl.setStatus(CrawlResultEntity.STATUS_SUCCESS);

		return crawl;
	}

	/**
	 * <p>
	 * クロール結果を保存する.
	 * </p>
	 * @param result 
	 */
	public void setResult(CrawlResultEntity result) {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			CrawlResultDao dao = new CrawlResultDao(executor);

			if (dao.isExist(result.getBlogUrl())) {
				dao.update(result);
			} else {
				dao.insert(result);
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * クロール結果を取得する.
	 * </p>
	 * @param blogUrl ブログのURL
	 * @return クロール結果
	 */
	public CrawlResultEntity getResult(String blogUrl) {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			CrawlResultDao dao = new CrawlResultDao(executor);
			return dao.select(blogUrl);
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * RSSアイテムからブログフィードを作成する.
	 * </p>
	 * @param blogUrl ブログのURL
	 * @param item RSSアイテム
	 * @return ブログフィード
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 * @throws ParserException
	 */
	private BlogFeedEntity getBlogFeed(String blogUrl, FeedItem item)
			throws NoSuchAlgorithmException, MalformedURLException, URISyntaxException, ParserException {

		// RSSアイテムデータの取得
		String link = item.getLink();
		Date pubDate = item.getPubDate();
		String authorId = AuthorIdGenerator.generate(blogUrl);
		String documentId = DocumentIdGenerator.generate(authorId, link);
		String description = item.getDescription();
		String content = item.getContentEncoded();

		// ブログフィードの作成
		BlogFeedEntity blogFeed = new BlogFeedEntity();
		blogFeed.setUrl(link);
		blogFeed.setTitle(item.getTitle());
		blogFeed.setDate(pubDate);
		blogFeed.setAuthorId(authorId);
		blogFeed.setDocumentId(documentId);
		blogFeed.setDescription(HtmlAnarizer.toPlaneText(description));
		blogFeed.setBody(HtmlAnarizer.toPlaneText(content));

		return blogFeed;
	}

	/**
	 * <p>
	 * ブログフィードを保存する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param entity ブログフィード
	 * @return true:追加, false:更新
	 */
	private boolean setBlogFeed(SQLExecutor executor, BlogFeedEntity entity) {
		BlogFeedDao dao = new BlogFeedDao(executor);
		if (dao.isExist(entity.getDocumentId())) {
			dao.update(entity);
			return false;
		} else {
			dao.insert(entity);
			return true;
		}
	}

	/**
	 * <p>
	 * クロールするかどうか判定する
	 * </p>
	 * @param pubDate ブログ日時
	 * @return true:クロールする, false:クロールしない
	 */
	private boolean isCrawl(Date pubDate) {

		if (pubDate == null) {
			return false;
		}

		// クロール開始日の取得
		int range = ResourceBundleUtil.getIntProperty("crawler", "crawl.date.range");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, -range);
		Date from = calendar.getTime();

		return pubDate.after(from);
	}

	/**
	 * <p>
	 * クロールするかどうか判定する
	 * </p>
	 * @param pubDate ブログ日時
	 * @param latestBlogDate 最新ブログ日時
	 * @return true:クロールする, false:クロールしない
	 */
	private boolean isCrawl(Date pubDate, Date latestBlogDate) {

		if (pubDate == null) {
			return false;
		}

		if (latestBlogDate == null) {
			return true;
		}

		return pubDate.after(latestBlogDate);
	}
}
