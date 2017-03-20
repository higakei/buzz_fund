package jp.co.hottolink.buzzfund.calendar.tools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.xml.sax.InputSource;

import jp.co.hottolink.buzzfund.calendar.dao.ToushiCalendarDao;
import jp.co.hottolink.buzzfund.calendar.tools.parser.CalendarSAXParser;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 投資カレンダーを作成するクラス.
 * </p>
 * @author higa
 */
public class CreateToushiCalendar {

	/**
	 * <p>
	 * デフォルトエンコーディング.
	 * </p>
	 */
	private static final String DEFAULT_ENCODINNG = "UTF-8";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;

		try {
			//Date from = DateUtil.parseToDate(args[0]);
			//Date to = DateUtil.parseToDate(args[1]);
			executor = new SQLExecutor("buzz_fund_db");
			//create(executor, from, to);
			//setHoliday(executor, from, to);
			//setToushouCalendar(executor);
			setMarketCalendar(executor);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * 指定した期間の投資カレンダーを作成する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param from 開始日
	 * @param to 終了日
	 */
	public static void create(SQLExecutor executor, Date from, Date to) {

		int start = DateUtil.getOffsetDay(from);
		int end = DateUtil.getOffsetDay(to);

		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		for (int i =start; i <= end; i++) {
			Date date = DateUtil.getOffsetDate(i);
			dao.insert(date);
		}
	}

	/**
	 * <p>
	 * 指定した期間の祝日を設定する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param from 開始日
	 * @param to　終了日
	 * @throws ParseException
	 */
	public static void setHoliday(SQLExecutor executor, Date from, Date to)
			throws ParseException {

		int start = DateUtil.getOffsetDay(from);
		int end = DateUtil.getOffsetDay(to);

		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		for (int i =start; i <= end; i++) {
			Date date = DateUtil.getOffsetDate(i);
			dao.updateHoliday(date);
		}
	}

	/**
	 * <p>
	 * 東証カレンダーを設定する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void setToushouCalendar(SQLExecutor executor)
			throws FileNotFoundException, UnsupportedEncodingException {

		// 入力ソースの作成
		String directory = ResourceBundleUtil.getProperty("calendar", "calendar.input.path");
		String file = directory + "toushou_calendar.xml";
		FileInputStream in = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(in, DEFAULT_ENCODINNG);
		InputSource source = new InputSource(reader);

		// XMLデータの取得
		CalendarSAXParser parser = new CalendarSAXParser();
		parser.parse(source);
		List<Map<String, Object>> calendar = parser.getCalendar();

		// カレンダーの設定
		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		for (Map<String, Object> day: calendar) {
			Date date = (Date)day.get("date");
			boolean isOpen = (Boolean)day.get("isOpen");

			Boolean dbIsOpen = dao.isOpen(date);
			if (dbIsOpen == null) {
				// 登録
				dao.insert(day);
				System.out.println("insert:" + date);
				continue;
			}

			// XMLデータが営業日の場合
			if (isOpen) {
				continue;
			}

			// DBが営業日の場合
			if (dbIsOpen) {
				// 休業日に更新
				dao.update(day);
				System.out.println("update:" + date);
			}
		}
	}

	/**
	 * <p>
	 * 国内市場のカレンダーを設定する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public static void setMarketCalendar(SQLExecutor executor)
			throws FileNotFoundException, UnsupportedEncodingException {

		// 入力ソースの作成
		String directory = ResourceBundleUtil.getProperty("calendar", "calendar.input.path");
		String file = directory + "market_calendar.xml";
		FileInputStream in = new FileInputStream(file);
		InputStreamReader reader = new InputStreamReader(in, DEFAULT_ENCODINNG);
		InputSource source = new InputSource(reader);
		
		// XMLデータの取得
		CalendarSAXParser parser = new CalendarSAXParser();
		parser.parse(source);
		List<Map<String, Object>> calendar = parser.getCalendar();
		
		// カレンダーの設定
		ToushiCalendarDao dao = new ToushiCalendarDao(executor);
		for (Map<String, Object> day: calendar) {
			Date date = (Date)day.get("date");
			boolean isOpen = (Boolean)day.get("isOpen");
		
			Boolean dbIsOpen = dao.isOpen(date);
			if (dbIsOpen == null) {
				// 登録
				dao.insert(day);
				System.out.println("insert:" + date);
				continue;
			}
		
			// XMLデータが営業日の場合
			if (isOpen) {
				continue;
			}
		
			// DBが営業日の場合
			if (dbIsOpen) {
				// 休業日に更新
				dao.update(day);
				System.out.println("update:" + date);
			}
		}
	}
}
