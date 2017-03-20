package jp.co.hottolink.buzzfund.crawler.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * RSSURLテーブルのDAOクラス
 * </p>
 * @author higa
 */
public class RssUrlDao {

	/**
	 * <p>
	 * 全レコードを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_ALL = "select blog_url, rss_url from rss_url";

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
	public RssUrlDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 全レコードを取得する.
	 * </p>
	 */
	public List<Map<String, String>> selectAll() {

		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_ALL);
			executor.executeQuery();

			// レコードの取得
			List<Map<String, String>> records = new ArrayList<Map<String,String>>();
			while(executor.next()) {
				Map<String, String> record = new HashMap<String, String>();
				record.put("blog_url", executor.getString("blog_url"));
				record.put("rss_url", executor.getString("rss_url"));
				records.add(record);
			}

			return records;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
