package jp.co.hottolink.buzzfund.calendar.dao;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.calendar.KtHoliday;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.DAOException;

/**
 * <p>
 * 投資カレンダーテーブルのDAOクラス.
 * </p>
 * @author higa
 */
public class ToushiCalendarDao {

	/**
	 * <p>
	 * 営業日.
	 * </p>
	 */
	private static final int OPEN = 1;

	/**
	 * <p>
	 * 休業日.
	 * </p>
	 */
	private static final int CLOSE = 2;

	/**
	 * <p>
	 * 指定した日のレコードを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT = "insert into toushi_calendar ("
			+ "date, day_of_week, open) values (?, ?, ?)";

	/**
	 * <p>
	 * 指定した日のレコードを登録するSQL.
	 * </p>
	 */
	private static final String SQL_INSERT_ALL = "insert into toushi_calendar ("
			+ "date, day_of_week, open, holiday) values (?, ?, ?, ?)";

	/**
	 * <p>
	 * 指定した日のレコードを更新するSQL.
	 * </p>
	 */
	private static final String SQL_UPDATE = "update toushi_calendar set"
			+ " day_of_week = ?, open = ?, holiday = ? where date = ?";

	/**
	 * <p>
	 * 祝日を設定するSQL.
	 * </p>
	 */
	private static final String SQL_UPDATE_HOLIDAY = "update toushi_calendar set"
			+ " open = ?, holiday = ? where date = ?";

	/**
	 * <p>
	 * 営業日を判定するSQL.
	 * </p>
	 */
	private static final String SQL_IS_OPEN =
			"select (open + 0) as open from toushi_calendar where date = ?";

	/**
	 * <p>
	 * 指定した日より、指定した日数の直近の過去を取得する.
	 * </p>
	 */
	private static final String SQL_SELECT_PRE_DAYS =
		"select date from toushi_calendar where date < ? and open = ? "
		+ "order by date desc limit ?";

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
	public ToushiCalendarDao(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 指定した日のレコードを登録する.
	 * </p>
	 * @param date 日
	 */
	public void insert(Date date) {
		try {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			// 曜日
			int dow = calendar.get(Calendar.DAY_OF_WEEK);
			// 営業日・休業日
			int open = ((dow == Calendar.SATURDAY) || (dow == Calendar.SUNDAY))
					? CLOSE : OPEN;

			// SQLの実行
			executor.preparedStatement(SQL_INSERT);
			int index = 0;
			executor.setDate(++index, date);
			executor.setInt(++index, dow);
			executor.setInt(++index, open);
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 指定した日のレコードを登録する.
	 * </p>
	 * @param day 日
	 */
	public void insert(Map<String, Object> day) {
		try {
			// データの取得
			Date date = (Date)day.get("date");
			String dow = (String)day.get("day_of_week");
			boolean isOpen = (Boolean)day.get("isOpen");
			String holiday = (String)day.get("holiday");

			// SQLの実行
			executor.preparedStatement(SQL_INSERT_ALL);
			int index = 0;
			executor.setDate(++index, date);
			executor.setString(++index, dow);
			executor.setInt(++index, isOpen ? OPEN:CLOSE);
			executor.setString(++index, holiday);
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 指定した日のレコードを更新する.
	 * </p>
	 * @param day 日
	 */
	public void update(Map<String, Object> day) {
		try {
			// データの取得
			Date date = (Date)day.get("date");
			String dow = (String)day.get("day_of_week");
			boolean isOpen = (Boolean)day.get("isOpen");
			String holiday = (String)day.get("holiday");

			// SQLの実行
			executor.preparedStatement(SQL_UPDATE);
			int index = 0;
			executor.setString(++index, dow);
			executor.setInt(++index, isOpen ? OPEN:CLOSE);
			executor.setString(++index, holiday);
			executor.setDate(++index, date);
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 祝日を設定する.
	 * </p>
	 * @param date 日
	 * @throws ParseException
	 */
	public void updateHoliday(Date date) throws ParseException {
		try {
			String holiday = KtHoliday.getHolidayName(date);
			if ((holiday == null) || holiday.isEmpty()) {
				return;
			}

			// SQLの実行
			executor.preparedStatement(SQL_UPDATE_HOLIDAY);
			int index = 0;
			executor.setInt(++index, CLOSE);
			executor.setString(++index, holiday);
			executor.setDate(++index, date);
			executor.executeUpdate();

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 営業日判定.
	 * </p>
	 * @param date 日
	 * @return 判定結果
	 */
	public Boolean isOpen(Date date) {
		try {
			executor.preparedStatement(SQL_IS_OPEN);
			executor.setDate(1, date);
			executor.executeQuery();
			if (!executor.next()) {
				return null;
			}

			int open = executor.getInt("open");
			return (open == OPEN);

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * 指定した日より過去の、指定した日数の直近営業日リストを取得する.
	 * </p>
	 * @param date 日
	 * @param days 日数
	 * @return 直近営業日リスト
	 */
	public List<Date> getPreOpenDates(Date date, int days) {
		try {
			// SQLの実行
			executor.preparedStatement(SQL_SELECT_PRE_DAYS);
			int index = 0;
			executor.setDate(++index, date);
			executor.setInt(++index, OPEN);
			executor.setInt(++index, days);
			executor.executeQuery();

			// 営業日リストの作成
			List<Date> dates = new ArrayList<Date>();
			if (executor.next()) {
				dates.add(executor.getDate("date"));
			}

			return dates;

		} catch (SQLException e) {
			throw new DAOException(e);
		} finally {
			executor.closeQuery();
		}
	}
}
