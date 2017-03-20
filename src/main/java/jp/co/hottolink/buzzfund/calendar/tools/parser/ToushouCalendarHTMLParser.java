package jp.co.hottolink.buzzfund.calendar.tools.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
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
 * 東証カレンダーのHTMLパーサークラス.
 * </p>
 * @author higa
 */
public class ToushouCalendarHTMLParser {

	/**
	 * <p>
	 * 東証カレンダーのURL.
	 * </p>
	 */
	private static final String URL_TOUSHOU_CALENDAR = "http://www.tse.or.jp/tseHpFront/HPTCDS0701.do";

	/**
	 * <p>
	 * デフォルトの日付パターン.
	 * </p>
	 */
	private static final String DEFAULT_DATE_PATTERN = "yyyyMMd";

	
	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 */
	public ToushouCalendarHTMLParser() {
		ConnectionManager manager = Parser.getConnectionManager();
		manager.setCookieProcessingEnabled(true);
	}

	/**
	 * <p>
	 * 選択月リストを取得する.
	 * </p>
	 * @return 選択月リスト
	 * @throws ParserException
	 * @throws IOException 
	 */
	public List<String> getSelMonths() throws ParserException, IOException {

		String url = URL_TOUSHOU_CALENDAR + "?method=init";
		HTMLParser parser = new HTMLParser();
		parser.setResource(url);

		// <select name="selMonth">の取得
		NodeFilter filter = new AndFilter(new NodeClassFilter(SelectTag.class)
				, new HasAttributeFilter("name", "selMonth"));
		NodeList nodes = parser.parse(filter);

		List<String> list = new ArrayList<String>();
		if (nodes.size() == 0) {
			return list;
		}

		// <option value="">以外のオプションを取得
		SelectTag select = (SelectTag)nodes.elementAt(0);
		OptionTag[] option = select.getOptionTags();
		for (int i = 0; i < option.length; i++) {
			String value = option[i].getValue();
			if (!value.isEmpty()) list.add(value);
		}

		return list;
	}

	/**
	 * <p>
	 * 選択月のデータを取得する.
	 * </p>
	 * @param firstMonth 開始月
	 * @param lastMonth 終了月
	 * @param selMonth 選択月
	 * @return 選択月のデータ
	 * @throws ParserException
	 * @throws ParseException
	 * @throws IOException 
	 */
	public Map<Date, Map<String, Object>> getMonth(String firstMonth, String lastMonth, String selMonth)
			throws ParserException, ParseException, IOException {

		// URLの作成
		StringBuffer url = new StringBuffer(URL_TOUSHOU_CALENDAR);
		url.append("?method=search&selCalendarType=00&addMonths=0");
		url.append("&firstMonth=" + firstMonth);
		url.append("&lastMonth=" + lastMonth);
		url.append("&selMonth=" + selMonth);

		HTMLParser parser = new HTMLParser();
		parser.setResource(url.toString());

		// <table class="calender">の取得
		NodeFilter filter = new AndFilter(
				new NodeClassFilter(TableTag.class),
				new HasAttributeFilter("class", "calender"));
		NodeList nodes = parser.parse(filter);

		Map<Date, Map<String, Object>> month = new TreeMap<Date, Map<String,Object>>();
		if (nodes.size() == 0) {
			return month;
		}

		// 選択月データの取得
		TableTag table = (TableTag)nodes.elementAt(0);
		TableRow[] row = table.getRows();
		for (int i = 0; i < row.length; i++) {
			String valign = row[i].getAttribute("valign");
			if (valign == null) {
				continue;
			}

			TableColumn[] column = row[i].getColumns();
			for (int j = 0; j < column.length; j++) {
				String text = column[j].toPlainTextString();
				if ("&nbsp;".equals(text)) {
					continue;
				}

				// 日付データの取得
				Map<String, Object> day = getDay(selMonth, column[j]);
				Date date  = (Date)day.get("date");
				month.put(date, day);
			}
		}

		return month;
	}

	/**
	 * <p>
	 * 日付データを取得する.
	 * </p>
	 * @param selMonth 選択月
	 * @param column tdタグ
	 * @return 日付データ
	 * @throws ParseException
	 */
	private Map<String, Object> getDay(String selMonth, TableColumn column)
			throws ParseException {

		// 日付の取得
		String text = column.toPlainTextString();
		text = text.replaceAll("[\\s]+", " ");
		text = text.trim();
		String[] splits = text.split(" ");
		String sDate = selMonth + splits[0];
		Date date = DateUtil.parseToDate(sDate, DEFAULT_DATE_PATTERN);

		// 市場休業日の取得
		boolean isOpen = true;
		String className = column.getAttribute("class");
		if ("bgColorRed2".equals(className)) {
			isOpen = false;
		}

		// 休日名の取得
		String holiday = KtHoliday.getHolidayName(date);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("date", date);
		map.put("isOpen", isOpen);
		if ((holiday != null) && !holiday.isEmpty()) map.put("holiday", holiday);

		return map;
	}
}
