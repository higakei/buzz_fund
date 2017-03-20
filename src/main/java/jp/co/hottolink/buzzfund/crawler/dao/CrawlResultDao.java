package jp.co.hottolink.buzzfund.crawler.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.hottolink.buzzfund.crawler.entity.CrawlResultEntity;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * クロール結果テーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class CrawlResultDao {

	/**
	 * <p>
	 * RSSのURLを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_RSS = "select rss_url from crawl_result where blog_url = ?";

	/**
	 * <p>
	 * RSSのURLを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT_RSS = "insert into crawl_result (blog_url, rss_url) values (?, ?)";

	/**
	 * <p>
	 * RSSのURLを更新するSQL.
	 * </p>
	 */
	private static final String SQL_UPDATE_RSS = "update crawl_result set rss_url = ? where blog_url = ?";

	/**
	 * <p>
	 * ブログのURLの登録をチェックするSQL.
	 * </p>
	 */
	private static final String SQL_IS_EXIST = "select id from crawl_result where blog_url = ?";

	/**
	 * <p>
	 * ブログのURLのレコードを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT = "select id, rss_url, total_crawl_count"
		+ ", latest_blog_date, feed_count, insert_count, (status + 0) as status, crawl_date"
		+ " from crawl_result where blog_url = ?";

	/**
	 * <p>
	 * ブログのURLのレコードを作成するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT = "insert into crawl_result ("
			+ "blog_url, rss_url, total_crawl_count, latest_blog_date"
			+ ", feed_count, insert_count, status"
			+ ") values (?, ?, ?, ?, ?, ?, ?)";

	/**
	 * <p>
	 * ブログのURLのレコードを更新するSQL.
	 * </p>
	 */
	private static final String SQL_UPDATE = "update crawl_result set"
			+ " blog_url = ?, rss_url = ?, total_crawl_count = ?, latest_blog_date = ?"
			+ ", feed_count = ?, insert_count = ?, status = ?, crawl_date = now()"
			+ " where id = ?";

	/**
	 * <p>
	 * 最新記事をクロールするブログのレコードを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_FOR_LATEST = "select id, blog_url"
			+ ", rss_url, total_crawl_count, latest_blog_date, feed_count"
			+ ", insert_count , (status + 0) as status, crawl_date"
			+ " from crawl_result where rss_url is not null and status = 1"
			+ " and total_crawl_count > 0 and latest_blog_date is not null";

	/**
	 * <p>
	 * SQL実行クラス.
	 * </p>
	 */
	private SQLExecutor executor = null;

	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public CrawlResultDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * RSSのURLを取得する
	 * </p>
	 * @param blogUrl ブログのURL
	 * @return RSSのURL
	 */
	public String getRss(String blogUrl) {
		try {
			executor.preparedStatement(SQL_SELECT_RSS);
			executor.setString(1, blogUrl);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			return executor.getString("rss_url");

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログのURLの登録をチェックする.
	 * </p>
	 * @param blogUrl ブログのURL
	 * @return true:登録, false:未登録
	 */
	public boolean isExist(String blogUrl) {
		try {
			executor.preparedStatement(SQL_IS_EXIST);
			executor.setString(1, blogUrl);
			executor.executeQuery();
			return executor.next();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * RSSのURLを登録するSQL.
	 * </p>
	 * @param blogUrl ブログのURL
	 * @param rssUrl RSSのURL
	 */
	public void insertRss(String blogUrl, String rssUrl) {
		try {
			executor.preparedStatement(SQL_INSERT_RSS);
			executor.setString(1, blogUrl);
			executor.setString(2, rssUrl);
			executor.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * RSSのURLを登録するSQL.
	 * </p>
	 * @param blogUrl ブログのURL
	 * @param rssUrl RSSのURL
	 */
	public void updateRss(String blogUrl, String rssUrl) {
		try {
			executor.preparedStatement(SQL_UPDATE_RSS);
			executor.setString(2, blogUrl);
			executor.setString(1, rssUrl);
			executor.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログのURLのレコードを取得する
	 * </p>
	 * @param blogUrl ブログのURL
	 * @return レコード
	 */
	public CrawlResultEntity select(String blogUrl) {
		try {
			executor.preparedStatement(SQL_SELECT);
			executor.setString(1, blogUrl);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			// レコードデータの取得
			CrawlResultEntity entity = new CrawlResultEntity();
			entity.setId(executor.getInt("id"));
			entity.setBlogUrl(blogUrl);
			entity.setRssUrl(executor.getString("rss_url"));
			entity.setTotalCrawlCount(executor.getInt("total_crawl_count"));
			entity.setLatestBlogDate(executor.getTimestamp("latest_blog_date"));
			entity.setFeedCount(executor.getInteger("feed_count"));
			entity.setInsertCount(executor.getInteger("insert_count"));
			entity.setStatus(executor.getInt("status"));
			entity.setCrawlDate(executor.getTimestamp("crawl_date"));

			return entity;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログのURLのレコードを作成する.
	 * </p>
	 * @param entity レコードデータ
	 */
	public void insert(CrawlResultEntity entity) {
		try {
			executor.preparedStatement(SQL_INSERT);
			int index = 0;
			executor.setString(++index, entity.getBlogUrl());
			executor.setString(++index, entity.getRssUrl());
			executor.setInt(++index, entity.getTotalCrawlCount());
			executor.setTimestamp(++index, entity.getLatestBlogDate());
			executor.setInteger(++index, entity.getFeedCount());
			executor.setInteger(++index, entity.getInsertCount());
			executor.setInt(++index, entity.getStatus());
			executor.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログのURLのレコードを更新する.
	 * </p>
	 * @param entity レコードデータ
	 */
	public void update(CrawlResultEntity entity) {
		try {
			executor.preparedStatement(SQL_UPDATE);
			int index = 0;
			executor.setString(++index, entity.getBlogUrl());
			executor.setString(++index, entity.getRssUrl());
			executor.setInt(++index, entity.getTotalCrawlCount());
			executor.setTimestamp(++index, entity.getLatestBlogDate());
			executor.setInteger(++index, entity.getFeedCount());
			executor.setInteger(++index, entity.getInsertCount());
			executor.setInt(++index, entity.getStatus());
			executor.setInt(++index, entity.getId());
			executor.executeUpdate();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 最新記事をクロールするブログのレコードを取得する.
	 * </p>
	 * @return ブログリスト
	 */
	public List<CrawlResultEntity> selectForLatest() {
		try {
			executor.executeQuery(SQL_SELECT_FOR_LATEST);
			List<CrawlResultEntity> list = new ArrayList<CrawlResultEntity>();

			while (executor.next()) {
				CrawlResultEntity entity = new CrawlResultEntity();
				entity.setId(executor.getInt("id"));
				entity.setBlogUrl(executor.getString("blog_url"));
				entity.setRssUrl(executor.getString("rss_url"));
				entity.setTotalCrawlCount(executor.getInt("total_crawl_count"));
				entity.setLatestBlogDate(executor.getTimestamp("latest_blog_date"));
				entity.setFeedCount(executor.getInteger("feed_count"));
				entity.setInsertCount(executor.getInteger("insert_count"));
				entity.setStatus(executor.getInt("status"));
				entity.setCrawlDate(executor.getTimestamp("crawl_date"));
				list.add(entity);
			}

			return list;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
