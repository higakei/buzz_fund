package jp.co.hottolink.buzzfund.web.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * predictionlogテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class PredictionLogDao {

	/**
	 * <p>
	 * デフォルトの日付フォーマット.
	 * </p>
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";

	/**
	 * <p>
	 * 指定したプライマリキーのレコードを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_BY_PKEY = "select * from predictionlog where predictdate = ?";

	/**
	 * <p>
	 * 指定した日までの最新レコードを取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_LATEST = "select * from predictionlog" +
			" join toushi_calendar on (predictionlog.predictdate = toushi_calendar.date)" +
			" where predictionlog.predictdate <= ? and toushi_calendar.open = 1" +
			" order by predictionlog.predictdate desc limit 1";

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
	public PredictionLogDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 指定したプライマリキーのレコードを取得する.
	 * </p>
	 * @param predictDate 予測日
	 * @return レコード
	 */
	public Map<String, Object> selectByPkey(Date predictDate) {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_BY_PKEY);
			executor.setDate(1, predictDate);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			// レコードデータの取得
			Map<String, Object> record = new HashMap<String, Object>();
			for (int i = 1; i <= executor.getColumnCount(); i++) {
				String key = executor.getColumnLabel(i);
				Object value = executor.getObject(i);
				record.put(key, value);
			}

			return record;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 指定したプライマリキーのレコードを取得する.
	 * </p>
	 * @param predictDate 予測日
	 * @return レコード
	 * @throws ParseException 
	 */
	public Map<String, Object> selectByPkey(String predictDate) throws ParseException {
		Date date = DateUtil.parseToDate(predictDate, DEFAULT_DATE_FORMAT);
		return selectByPkey(date);
	}

	/**
	 * <p>
	 * 指定した日までの最新レコードを取得するSQL.
	 * </p>
	 * @param predictDate 予測日
	 * @return 最新レコード
	 */
	public Map<String, Object> selectLatest(Date predictDate) {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_LATEST);
			executor.setDate(1, predictDate);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			// レコードデータの取得
			Map<String, Object> record = new HashMap<String, Object>();
			for (int i = 1; i <= executor.getColumnCount(); i++) {
				String key = executor.getColumnLabel(i);
				Object value = executor.getObject(i);
				record.put(key, value);
			}

			return record;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
