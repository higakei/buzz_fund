package jp.co.hottolink.buzzfund.reputation.dao;

import java.sql.SQLException;

import jp.co.hottolink.buzzfund.reputation.entity.ReputationCountEntity;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * 評判情報テーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class ReputationCountDao {

	/**
	 * <p>
	 * レコードデータを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT = "insert into reputation_count"
			+ " (date, word, splog, positive, flat, negative, total, result_status)"
			+ " values (?, ?, ?, ?, ?, ?, ?, ?)";

	/**
	 * <p>
	 * レコードデータが重複しているかどうか判定するSQL.
	 * </p>
	 */
	private static final String SQL_IS_DUPLICATE = "select id from reputation_count"
			+ " where date = ? and word = ? and splog = ? and positive = ?"
			+ " and flat = ? and negative = ? and total = ? and result_status = ?";

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
	public ReputationCountDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * レコードデータを登録する.
	 * </p>
	 * @param entity レコードデータ
	 */
	public void insert(ReputationCountEntity entity) {
		try {
			// PreparedStatementの取得
			executor.preparedStatement(SQL_INSERT);

			// パラメータの設定
			int index = 0;
			executor.setDate(++index, entity.getDate());
			executor.setString(++index, entity.getWord());
			executor.setInteger(++index, entity.getSplog());
			executor.setInt(++index, entity.getPositive());
			executor.setInt(++index, entity.getFlat());
			executor.setInt(++index, entity.getNegative());
			executor.setInt(++index, entity.getTotal());
			executor.setString(++index, entity.getResultStatus());

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
	 * レコードデータが重複しているかどうか判定する.
	 * </p>
	 * @param entity レコードデータ
	 * @return <code>true</code>:重複, <code>false</code>:非重複
	 */
	public boolean isDuplicate(ReputationCountEntity entity) {
		try {
			// PreparedStatementの取得
			executor.preparedStatement(SQL_IS_DUPLICATE);

			// パラメータの設定
			int index = 0;
			executor.setDate(++index, entity.getDate());
			executor.setString(++index, entity.getWord());
			executor.setInteger(++index, entity.getSplog());
			executor.setInt(++index, entity.getPositive());
			executor.setInt(++index, entity.getFlat());
			executor.setInt(++index, entity.getNegative());
			executor.setInt(++index, entity.getTotal());
			executor.setString(++index, entity.getResultStatus());

			// SQLの実行
			executor.executeQuery();

			return executor.next();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
