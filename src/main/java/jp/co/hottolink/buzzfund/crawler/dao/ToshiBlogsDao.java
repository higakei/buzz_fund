package jp.co.hottolink.buzzfund.crawler.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * toshiblogsテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class ToshiBlogsDao {

	/**
	 * <p>
	 * toshiblogsテーブルからクロールするURLを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_TOSHI_BLOGS = "select url from toshiblogs";

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
	public ToshiBlogsDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * toshiblogsテーブルからクロールするURLを取得する.
	 * </p>
	 * @return クロールするURL
	 */
	public List<String> getCrawlUrls() {
		try {
			executor.executeQuery(SQL_SELECT_TOSHI_BLOGS);
			List<String> list = new ArrayList<String>();
			while (executor.next()) list.add(executor.getString("url"));
			return 	list;	
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
