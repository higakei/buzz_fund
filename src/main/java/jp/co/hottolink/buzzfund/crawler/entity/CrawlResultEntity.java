package jp.co.hottolink.buzzfund.crawler.entity;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * クロール結果テーブルのEntityクラス.
 * </p>
 * @author higa
 */
public class CrawlResultEntity implements Serializable {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = 6108497885310554442L;

	/**
	 * <p>
	 * ステータス:成功
	 * </p>
	 */
	public static final int STATUS_SUCCESS = 1;

	/**
	 * <p>
	 * ステータス:RSSのURLなし.
	 * </p>
	 */
	public static final int STATUS_RSS_URL_NOT_FOUND = 2;

	/**
	 * <p>
	 * ステータス:RSSフィード取得エラー.
	 * </p>
	 */
	public static final int STATUS_FEED_FAILED = 3;

	/**
	 * <p>
	 * ステータス:RSSのURL取得エラー.
	 * </p>
	 */
	public static final int STATUS_RSS_URL_FAILED = 4;

	/**
	 * <p>
	 * ステータス:RSSのURL取得エラー.
	 * </p>
	 */
	public static final int STATUS_PAGE_NOT_FOUND = 5;

	/**
	 * <p>
	 * ID.
	 * </p>
	 */
	private int id = 0;

	/**
	 * <p>
	 * ブログのURL.
	 * </p>
	 */
	private String blogUrl = null;

	/**
	 * <p>
	 * RSSのURL.
	 * </p>
	 */
	private String rssUrl = null;

	/**
	 * <p>
	 * 総クロール数.
	 * </p>
	 */
	private int totalCrawlCount = 0;

	/**
	 * <p>
	 * 最新ブログ日時.
	 * </p>
	 */
	private Timestamp latestBlogDate = null;

	/**
	 * <p>
	 * フィード数.
	 * </p>
	 */
	private Integer feedCount = null;

	/**
	 * <p>
	 * 追加したフィード数.
	 * </p>
	 */
	private Integer insertCount = null;

	/**
	 * <p>
	 * クロール日時.
	 * </p>
	 */
	private Timestamp crawlDate = null;

	/**
	 * <p>
	 * ステータス.
	 * </p>
	 */
	private int status = 0;

	/**
	 * <p>
	 * ブログのURLを取得する.
	 * </p>
	 * @return URL
	 */
	public String getBlogUrl() {
		return blogUrl;
	}

	/**
	 * <p>
	 * ブログのURLを設定する.
	 * </p>
	 * @param blogUrl URL
	 */
	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
	}

	/**
	 * <p>
	 * RSSのURLを取得する.
	 * </p>
	 * @return RSSのURL
	 */
	public String getRssUrl() {
		return rssUrl;
	}

	/**
	 * <p>
	 * RSSのURLを設定する.
	 * </p>
	 * @param rssUrl RSSのURL
	 */
	public void setRssUrl(String rssUrl) {
		this.rssUrl = rssUrl;
	}

	/**
	 * <p>
	 * 総クロール数を取得する.
	 * </p>
	 * @return 総クロール数
	 */
	public int getTotalCrawlCount() {
		return totalCrawlCount;
	}

	/**
	 * <p>
	 * 総クロール数を設定する.
	 * </p>
	 * @param totalCrawlCount 総クロール数
	 */
	public void setTotalCrawlCount(int totalCrawlCount) {
		this.totalCrawlCount = totalCrawlCount;
	}

	/**
	 * <p>
	 * 最新ブログ日時を取得する.
	 * </p>
	 * @return 最新ブログ日時
	 */
	public Timestamp getLatestBlogDate() {
		return latestBlogDate;
	}

	/**
	 * <p>
	 * 最新ブログ日時を設定する.
	 * </p>
	 * @param latestBlogDate 最新ブログ日時
	 */
	public void setLatestBlogDate(Timestamp latestBlogDate) {
		this.latestBlogDate = latestBlogDate;
	}

	/**
	 * <p>
	 * フィード数を取得する.
	 * </p>
	 * @return フィード数
	 */
	public Integer getFeedCount() {
		return feedCount;
	}

	/**
	 * <p>
	 * フィード数を設定する.
	 * </p>
	 * @param feedCount フィード数
	 */
	public void setFeedCount(Integer feedCount) {
		this.feedCount = feedCount;
	}

	/**
	 * <p>
	 * 追加したフィード数を取得する.
	 * </p>
	 * @return 追加したフィード数
	 */
	public Integer getInsertCount() {
		return insertCount;
	}

	/**
	 * <p>
	 * 追加したフィード数を設定する.
	 * </p>
	 * @param insertCount 追加したフィード数
	 */
	public void setInsertCount(Integer insertCount) {
		this.insertCount = insertCount;
	}

	/**
	 * <p>
	 * クロール日時を取得する.
	 * </p>
	 * @return crawlDate
	 */
	public Timestamp getCrawlDate() {
		return crawlDate;
	}

	/**
	 * <p>
	 * クロール日時を設定する.
	 * </p>
	 * @param crawlDate クロール日時
	 */
	public void setCrawlDate(Timestamp crawlDate) {
		this.crawlDate = crawlDate;
	}

	/**
	 * <p>
	 * IDを取得する.
	 * </p>
	 * @return ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * <p>
	 * IDを設定する.
	 * </p>
	 * @param id ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * <p>
	 * ステータスを取得する.
	 * </p>
	 * @return ステータス
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * <p>
	 * ステータスを設定する.
	 * </p>
	 * @param status ステータス
	 */
	public void setStatus(int status) {
		this.status = status;
	}
}
