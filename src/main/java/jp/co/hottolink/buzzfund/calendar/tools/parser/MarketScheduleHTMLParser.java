package jp.co.hottolink.buzzfund.calendar.tools.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.htmlparser.NodeFilter;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.NotFilter;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jp.co.hottolink.buzzfund.calendar.KtHoliday;
import jp.co.hottolink.buzzfund.common.parser.HTMLParser;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 国内市場スケジュールのHTMLパーサークラス.
 * </p>
 * @author higa
 */
public class MarketScheduleHTMLParser {

	/**
	 * <p>
	 *  国内市場スケジュールのURL.
	 * </p>
	 */
	private static final String URL_MARKET_SCHEDULE = "http://www.traders.co.jp/domestic_stocks/domestic_market/market_s/";

	/**
	 * <p>
	 * 日付パターン.
	 * </p>
	 */
	private static final String DATE_PATTERN = "yyyy年M月d";

	/**
	 * <p>
	 * バックナンバーのリンクを取得する.
	 * </p>
	 * @return バックナンバーのリンク
	 * @throws ParserException
	 * @throws IOException 
	 */
	public static List<String> getBackNumber() throws ParserException, IOException {

		String url = URL_MARKET_SCHEDULE + "market_s_bn_list.asp";
		HTMLParser parser = new HTMLParser();
		parser.setResource(url);

		// <a href>の取得
		NodeFilter filter = new AndFilter(new HasAttributeFilter("href")
			, new HasParentFilter(new HasAttributeFilter("class", "contents2"), true));
		NodeList nodes = parser.parse(filter);

		// バックナンバーのリンクの取得
		List<String> backNumber = new ArrayList<String>();
		for (int i = 0; i < nodes.size(); i++) {
			LinkTag tag = (LinkTag)nodes.elementAt(i);
			backNumber.add(tag.getLink());
		}

		return backNumber;
	}

	/**
	 * <p>
	 * 月のデータを取得する.
	 * </p>
	 * @param link バックナンバーのリンク
	 * @return 月のデータ
	 * @throws ParserException
	 * @throws ParseException
	 * @throws IOException 
	 */
	public static Map<Date, Map<String, Object>> getMonth(String link)
			throws ParserException, ParseException, IOException {

		String url = URL_MARKET_SCHEDULE + link;
		HTMLParser parser = new HTMLParser();
		parser.setResource(url);

		// <div class="contents2">の取得
		NodeFilter filter = new AndFilter(new NodeClassFilter(TableTag.class)
			, new HasParentFilter(new HasAttributeFilter("class", "contents2")));
		NodeList nodes = parser.parse(filter);

		Map<Date, Map<String, Object>> month = new TreeMap<Date, Map<String,Object>>();
		if (nodes.size() == 0) {
			return month;
		}

		// <table>の取得
		TableTag table = (TableTag)nodes.elementAt(0);
		TableRow row = table.getRow(0);
		TableColumn column = row.getColumns()[0];

		// <table>の取得
		table = (TableTag)column.getChild(1);
		row = table.getRow(0);
		column = row.getColumns()[0];

		// 年月の取得
		table = (TableTag)column.getChild(3);
		row = table.getRow(1);
		column = row.getColumns()[0];
		String ym = column.toPlainTextString();

		// 日の取得
		nodes.removeAll();
		filter = new AndFilter(new HasChildFilter(new HasAttributeFilter("bgcolor", "C9D3D8")),
				new NotFilter(new HasChildFilter(new HasAttributeFilter("bgcolor", "DBE7FC"))));
		table.collectInto(nodes, filter);
		for (int i = 0; i < nodes.size(); i++) {
			Map<String, Object> day = getDay(ym, (TableRow)nodes.elementAt(i));
			if (day == null) continue;
			Date date  = (Date)day.get("date");

			if (!month.containsKey(date)) {
				month.put(date, day);
			}
		}

		return month;
	}

	/**
	 * <p>
	 * 日付データを作成する.
	 * </p>
	 * @param ym 年月
	 * @param row 日付データ行
	 * @return 日付データ
	 * @throws ParseException
	 */
	private static Map<String, Object> getDay(String ym, TableRow row) 
			throws ParseException {

		// 日付
		TableColumn column = row.getColumns()[0];
		String text = column.toPlainTextString();
		String day = null;

		// 通常パターン
		Pattern pattern = Pattern.compile("([0-9]{1,2})");
		Matcher matcher = pattern.matcher(text);
		if (matcher.find()) {
			day = matcher.group(1);
		}

		if (day == null) {
			//System.out.println(ym + ":" + text);
			return null;
		}

		Date date = null;
		try {
			date = DateUtil.parseToDate(ym + day, DATE_PATTERN);
		} catch (ParseException e) {
			//e.printStackTrace();
			return null;
		}

		// 休日名の取得
		String holiday = KtHoliday.getHolidayName(date);
		if ((holiday == null) || holiday.isEmpty()) holiday = null;

		// 営業日・休業日
		column = row.getColumns()[2];
		text = column.toPlainTextString();
		boolean isOpen = false;
		if (isWeekEnd(date)) {
			isOpen = false;
		} else if (holiday != null) {
			isOpen = false;
		} else if (text.indexOf("休場") > -1) {
			isOpen = false;
		} else {
			isOpen = true;
		}

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("date", date);
		map.put("isOpen", isOpen);
		if (holiday != null) map.put("holiday", holiday);

		return map;
	}

	/**
	 * <p>
	 * 週末かどうか判定する.
	 * </p>
	 * @param date 日付
	 * @return <code>true</code>:週末である, <code>false</code>:週末でない
	 */
	private static boolean isWeekEnd(Date date) {

		if (date == null) {
			return false;
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		int dow = calendar.get(Calendar.DAY_OF_WEEK);

		return (dow == Calendar.SATURDAY) || (dow == Calendar.SUNDAY);
	}
}
