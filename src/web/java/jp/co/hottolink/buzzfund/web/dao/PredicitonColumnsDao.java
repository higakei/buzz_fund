package jp.co.hottolink.buzzfund.web.dao;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * prediction_columnsテーブルのDAOクラス.
 * </p>
 * @author higa
 *
 */
public class PredicitonColumnsDao {

	/**
	 * <p>
	 * SQL実行クラス.
	 * </p>
	 */
	private SQLExecutor executor = null;

	/**
	 * <p>
	 * 画面表示するカラムを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_VISIBILE = "select * from prediction_columns where visibility = 'visible'";

	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public PredicitonColumnsDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 画面表示するカラムを取得する.
	 * </p>
	 * @return テーブルデータ
	 */
	public Map<String, Map<String, Object>> selectVisible() {
		try {
			// SQLの実行
			executor.executeQuery(SQL_SELECT_VISIBILE);

			Map<String, Map<String, Object>> table = new HashMap<String, Map<String,Object>>();
			while (executor.next()) {
				// テーブルデータの取得
				Map<String, Object> record = new HashMap<String, Object>();
				for (int i = 1; i <= executor.getColumnCount(); i++) {
					String key = executor.getColumnLabel(i);
					Object value = executor.getObject(i);
					record.put(key, value);
				}

				String key = (String)record.get("column");
				table.put(key, record);
			}

			return table;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
