package jp.co.hottolink.buzzfund.crawler.dao;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;

import jp.co.hottolink.buzzfund.crawler.entity.BlogFeedEntity;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * ブログフィードテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class BlogFeedDao {

	/**
	 * <p>
	 * ブログフィードを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT = "insert into blogfeedRDF" +
			" (documentId, authorId, url, title, body, date, description, dateindex)" +
			" values (?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * <p>
	 * ブログフィードを更新するSQL.
	 * </p>
	 */
	private static final String SQL_UPDATE = "update blogfeedRDF set" +
			" authorId = ?, url = ?, title = ?, body = ?, date = ?, description = ?, dateindex = ?" +
			" where documentId = ?";

	/**
	 * <p>
	 * ブログフィードを登録チェックするSQL.
	 * </p>
	 */
	private static final String SQL_IS_EXIST = "select documentId from blogfeedRDF where documentId = ?";

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
	public BlogFeedDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * ブログフィードを登録する.
	 * </p>
	 * @param entity ブログフィード
	 */
	public void insert(BlogFeedEntity entity) {
		try {
			// 公開日の設定
			Date date = null;
			Timestamp timestamp = entity.getDate();
			if (timestamp != null) {
				date = new Date(entity.getDate().getTime());
			}

			// PreparedStatementの取得
			executor.preparedStatement(SQL_INSERT);

			// パラメータの設定
			int index = 0;
			executor.setString(++index, entity.getDocumentId());
			executor.setString(++index, entity.getAuthorId());
			executor.setString(++index, entity.getUrl());
			executor.setString(++index, entity.getTitle());
			executor.setString(++index, entity.getBody());
			executor.setTimestamp(++index, entity.getDate());
			executor.setString(++index, entity.getDescription());
			executor.setDate(++index, date);

			// SQLの実行
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログフィードを更新する.
	 * </p>
	 * @param entity ブログフィード
	 */
	public void update(BlogFeedEntity entity) {
		try {
			// 公開日の設定
			Date date = null;
			Timestamp timestamp = entity.getDate();
			if (timestamp != null) {
				date = new Date(entity.getDate().getTime());
			}

			// PreparedStatementの取得
			executor.preparedStatement(SQL_UPDATE);

			// パラメータの設定
			int index = 0;
			executor.setString(++index, entity.getAuthorId());
			executor.setString(++index, entity.getUrl());
			executor.setString(++index, entity.getTitle());
			executor.setString(++index, entity.getBody());
			executor.setTimestamp(++index, entity.getDate());
			executor.setString(++index, entity.getDescription());
			executor.setDate(++index, date);
			executor.setString(++index, entity.getDocumentId());

			// SQLの実行
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * ブログフィードを登録チェックする.
	 * </p>
	 * @param documentId 文書ID
	 * @return true:登録済, false:未登録
	 */
	public boolean isExist(String documentId) {
		try {
			executor.preparedStatement(SQL_IS_EXIST);
			executor.setString(1, documentId);
			executor.executeQuery();
			return executor.next();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
