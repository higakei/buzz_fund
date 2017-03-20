package jp.co.hottolink.buzzfund.crawler.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * 為替レートテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class FxRateDao {

	/**
	 * <p>
	 * レコードを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT = "insert into fx_rate" +
			" (currency, date, open, high, low, close) " +
			" values (?, ?, ?, ?, ?, ?)";

	/**
	 * <p>
	 * レコードの重複を判定するSQL.
	 * </p>
	 */
	private static final String SQL_IS_DUPLICATE = "select currency, date from fx_rate where currency = ? and date = ?";

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
	public FxRateDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * レコードを登録する.
	 * </p>
	 * @param record レコード
	 */
	public void insert(Map<String, Object> record) {
		try {
			// データの取得
			String currency = (String)record.get("currency");
			Date date = (Date)record.get("date");
			Double open = (Double)record.get("open");
			Double high = (Double)record.get("high");
			Double low = (Double)record.get("low");
			Double close = (Double)record.get("close");

			// SQLの実行
			executor.preparedStatement(SQL_INSERT);
			int index = 0;
			executor.setString(++index, currency);
			executor.setDate(++index, date);
			executor.setDouble(++index, open);
			executor.setDouble(++index, high);
			executor.setDouble(++index, low);
			executor.setDouble(++index, close);
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * レコードの重複を判定する.
	 * </p>
	 * @param record レコード
	 * @return <code>true</code>:重複、<code>false</code>:一意
	 */
	public boolean isDuplicate(Map<String, Object> record) {
		String currency = (String)record.get("currency");
		Date date = (Date)record.get("date");
		return isDuplicate(currency, date);
	}

	/**
	 * <p>
	 * レコードの重複を判定する.
	 * </p>
	 * @param currency 通貨
	 * @param date 日付
	 * @return <code>true</code>:重複、<code>false</code>:一意
	 */
	public boolean isDuplicate(String currency, Date date) {
		try {
			executor.preparedStatement(SQL_IS_DUPLICATE);
			int index = 0;
			executor.setString(++index, currency);
			executor.setDate(++index, date);
			executor.executeQuery();
			return executor.next();
		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
