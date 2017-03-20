package jp.co.hottolink.buzzfund.web.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * フィボナッチ比率テーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class FibonacciRatioDao {

	/**
	 * <p>
	 * フィボナッチ比率の昇順でテーブルデータを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_IN_RATIO_ASC = "select * from fibonacci_ratio order by ratio asc";

	/**
	 * <p>
	 * フィボナッチ比率の降順でテーブルデータを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_IN_RATIO_DESC = "select * from fibonacci_ratio order by ratio desc";

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
	public FibonacciRatioDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * フィボナッチ比率の昇順でテーブルデータを取得する.
	 * </p>
	 * @return テーブルデータ
	 */
	public List<Map<String, Object>> selectInRatioAsc() {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_IN_RATIO_ASC);
			executor.executeQuery();

			ArrayList<Map<String, Object>> table = new ArrayList<Map<String,Object>>();
			while (executor.next()) {
				// テーブルデータの取得
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= executor.getColumnCount(); i++) {
					String key = executor.getColumnLabel(i);
					Object value = executor.getObject(i);
					record.put(key, value);
				}
				table.add(record);
			}

			return table;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * フィボナッチ比率の降順でテーブルデータを取得する.
	 * </p>
	 * @return テーブルデータ
	 */
	public List<Map<String, Object>> selectInRatioDesc() {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_IN_RATIO_DESC);
			executor.executeQuery();

			// テーブルデータの取得
			ArrayList<Map<String, Object>> table = new ArrayList<Map<String,Object>>();
			while (executor.next()) {
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= executor.getColumnCount(); i++) {
					String key = executor.getColumnLabel(i);
					Object value = executor.getObject(i);
					record.put(key, value);
				}
				table.add(record);
			}

			return table;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
