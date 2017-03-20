package jp.co.hottolink.buzzfund.calendar.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.calendar.KtHoliday;
import jp.co.hottolink.buzzfund.calendar.dao.ToushiCalendarDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * 投資カレンダーのUtilityクラス.
 * </p>
 * @author higa
 */
public class ToushiCalendarUtil {

	/**
	 * <p>
	 * 営業日判定.
	 * </p>
	 * @param date 日
	 * @return <code>true</code>:営業日, <code>false</code>休業日
	 * @throws ParseException
	 */
	public static boolean isOpen(Date date) throws ParseException {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);

		// 土日
		int dow = calendar.get(Calendar.DAY_OF_WEEK);
		if ((dow == Calendar.SATURDAY) || (dow == Calendar.SUNDAY)) {
			return false;
		}

		// 祝日
		String holiday = KtHoliday.getHolidayName(date);
		if ((holiday != null) && !holiday.isEmpty()) {
			return false;
		}

		return true;
	}

	/**
	 * <p>
	 * 営業日判定.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param date 日
	 * @return <code>true</code>:営業日, <code>false</code>休業日
	 * @throws ParseException
	 */
	public static boolean isOpen(SQLExecutor executor, Date date)
			throws ParseException {

		// DBに問い合わせ
		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		Boolean isOpen = dao.isOpen(date);

		// DBの範囲外
		if (isOpen == null) {
			return isOpen(date);
		} 

		return isOpen;
	}

	/**
	 * <p>
	 * 前営業日を取得する.
	 * </p>
	 * @param date 指定日
	 * @return 前営業日
	 * @throws ParseException
	 */
	public static Date getPreOpenDate(Date date) throws ParseException {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DATE, -1);
		Date preDate = calendar.getTime();

		if (isOpen(preDate)) {
			// 営業日
			return preDate;
		} else {
			// 休業日
			return getPreOpenDate(preDate);
		}
	}

	/**
	 * <p>
	 * 前営業日を取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param date 指定日
	 * @return 前営業日
	 * @throws ParseException
	 */
	public static Date getPreOpenDate(SQLExecutor executor, Date date)
			throws ParseException {

		// DBから取得
		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		List<Date> preOpenDays = dao.getPreOpenDates(date, 1);

		// DBの範囲外
		if (preOpenDays.size() == 1) {
			return preOpenDays.get(0);
		}

		return getPreOpenDate(date);
	}

	/**
	 * <p>
	 * 指定した日より過去の、指定した日数の、直近の営業期間を取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param date 日
	 * @param days 日数
	 * @return 営業期間
	 * @throws ParseException
	 */
	public static Map<String, Date> getPreOpenPeriod(SQLExecutor executor,
			Date date, int days) throws ParseException {

		// DBから取得
		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		List<Date> preOpenDays = dao.getPreOpenDates(date, days);

		Map<String, Date> period = new HashMap<String, Date>();
		Date openDate = date;

		for (int i = 0; i < days; i++) {
			if (i < preOpenDays.size()) {
				openDate = preOpenDays.get(i);
			} else {
				// DBの範囲外
				openDate = getPreOpenDate(openDate);
			}

			// 終了日の設定
			if (i == 0) {
				period.put("to", openDate);
			}
		}

		period.put("from", openDate);
		return period;
	}
}
