package jp.co.hottolink.buzzfund.web.dao;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * priceテーブルとpriceeveningテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class PriceDao {

	/**
	 * <p>
	 * デフォルトの日付フォーマット.
	 * </p>
	 */
	private static final String DEFAULT_DATE_FORMAT = "yyyy-M-d";

	/**
	 * <p>
	 * 指定した期間の最高値と最安値を取得するSQL.
	 * </p>
	 */
	private static final String SQL_SELECT_MAX_MIN = "select max(high) as max, min(low) as min"
			+ " from (select high, low from price where date between ? and ?"
			+ " union all select high, low from priceevening where date between ? and ?"
			+ " ) as price_union";

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
	public PriceDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 指定した期間の最高値と最安値を取得する.
	 * </p>
	 * @param from 開始日
	 * @param to 終了日
	 * @return 最高値と最安値
	 */
	public Map<String, Integer> selectMaxMin(Date from, Date to) {
		String sFrom = DateUtil.toString(from, DEFAULT_DATE_FORMAT);
		String sTo = DateUtil.toString(to, DEFAULT_DATE_FORMAT);
		return selectMaxMin(sFrom, sTo);
	}

	/**
	 * <p>
	 * 指定した期間の最高値と最安値を取得する.
	 * </p>
	 * @param from 開始日
	 * @param to 終了日
	 * @return 最高値と最安値
	 */
	public Map<String, Integer> selectMaxMin(String from, String to) {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_MAX_MIN);
			int index = 0;
			executor.setString(++index, from);
			executor.setString(++index, to);
			executor.setString(++index, from);
			executor.setString(++index, to);
			executor.executeQuery();
			executor.next();

			// 最高値と最安値の取得
			Integer max = executor.getInteger("max");
			Integer min = executor.getInteger("min");
			if ((max == null) || (min == null)) {
				return null;
			}

			// 最高値と最安値の設定
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("max", max);
			map.put("min", min);

			return map;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
