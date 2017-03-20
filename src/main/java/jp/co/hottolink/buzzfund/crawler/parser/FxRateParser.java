package jp.co.hottolink.buzzfund.crawler.parser;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.htmlparser.Node;
import org.htmlparser.NodeFilter;
import org.htmlparser.Text;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasChildFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.filters.OrFilter;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import jp.co.hottolink.buzzfund.common.parser.HTMLParser;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 為替レートのパーサークラス.
 * </p>
 * @author higa
 */
public class FxRateParser {

	/**
	 * <p>
	 * 日別の時系列データのURL.
	 * </p>
	 */
	private static final String DATE_URL = "http://money.www.infoseek.co.jp/MnForex/fxlast.html?k=d";

	/**
	 * <p>
	 * HTMLの日付パターン.
	 * </p>
	 */
	private static final String HTML_DATE_PATTERN = "yyyy年MM月dd日";

	/**
	 * <p>
	 * 時系列データの項目名.
	 * </p>
	 */
	private static final String[] RATE_COLUMNS = {"日付", "終値", "始値", "高値", "安値"};

	/**
	 * <p>
	 * 為替レートを取得する.
	 * </p>
	 * @param currency 通貨
	 * @param from 開始日
	 * @param to 終了日
	 * @return 為替レート
	 * @throws ParserException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static List<Map<String, Object>> getRates(String currency, Date from,
			Date to) throws ParserException, IOException, ParseException {

		// 時系列データのURLの取得
		String url = getUrl(currency, from, to);

		// 時系列データの<table>の取得
		HTMLParser parser = HTMLParser.createParser(url);
		NodeFilter filter = new AndFilter(new NodeClassFilter(TableTag.class)
				, new HasChildFilter(getTableRowFilter(RATE_COLUMNS)));
		NodeList nodes = parser.parse(filter);

		List<Map<String, Object>> rates = new ArrayList<Map<String,Object>>();
		if (nodes.size() == 0) {
			return rates;
		}

		// 項目名の取得
		TableTag table = (TableTag)nodes.elementAt(0);
		TableRow[] rows = table.getRows();
		String[] columns = getColumns(rows[0]);

		// 為替レートの取得
		for (int i = 1; i < rows.length; i++) {
			Map<String, Object> rate = getRate(rows[i], columns);
			rate.put("currency", currency);
			rates.add(rate);
		}

		return rates;
	}

	/**
	 * <p>
	 * 時系列データのURLを取得する.
	 * </p>
	 * @param currency 通貨
	 * @param from 開始日
	 * @param to 終了日
	 * @return URL
	 */
	private static String getUrl(String currency, Date from, Date to) {

		String fx = ResourceBundleUtil.getProperty("infoseek", currency);
		StringBuffer url = new StringBuffer(DATE_URL);
		url.append("&fx=" + fx);

		if ((from != null) && (to != null)) {
			url.append("&sy=" + DateUtil.toString(from, "yyyy"));
			url.append("&sm=" + DateUtil.toString(from, "M"));
			url.append("&sd=" + DateUtil.toString(from, "d"));
			url.append("&ey=" + DateUtil.toString(to, "yyyy"));
			url.append("&em=" + DateUtil.toString(to, "M"));
			url.append("&ed=" + DateUtil.toString(to, "d"));
		}

		return url.toString();
	}

	/**
	 * <p>
	 * 指定した項目名のtrタグのNodeFilterを取得する.
	 * </p>
	 * @param columns 項目名
	 * @return NodeFilter
	 */
	private static NodeFilter getTableRowFilter(String[] columns) {

		List<NodeFilter> filters = new ArrayList<NodeFilter>();
		filters.add(new NodeClassFilter(TableRow.class));
		if ((columns == null) || columns.length == 0) {
			return filters.get(0);
		}

		for (String column: columns) {
			filters.add(new HasChildFilter(getTableHeaderFilter(column)));
		}

		return new AndFilter(filters.toArray(new NodeFilter[0]));
	}

	/**
	 * <p>
	 * 時系列データの項目名を取得する.
	 * </p>
	 * @param row 時系列データ
	 * @return 項目名
	 */
	private static String[] getColumns(TableRow row) {

		NodeList nodes = new NodeList();
		NodeFilter filter = new NodeClassFilter(TableHeader.class);
		row.collectInto(nodes, filter);

		ArrayList<String> columns = new ArrayList<String>();
		for (Node node: nodes.toNodeArray()) {
			columns.add(node.toPlainTextString());
		}

		return columns.toArray(new String[0]);
	}

	/**
	 * <p>
	 * 為替レートを取得する.
	 * </p>
	 * @param row 時系列データ
	 * @param columns 項目名
	 * @return 為替レート
	 * @throws ParseException
	 */
	private static Map<String, Object> getRate(TableRow row, String[] columns)
			throws ParseException {

		// <td>,<th>の取得
		NodeList nodes = new NodeList();
		NodeFilter filter = new OrFilter(new NodeClassFilter(TableHeader.class)
				, new NodeClassFilter(TableColumn.class));
		row.collectInto(nodes, filter);

		Map<String, Object> map = new HashMap<String, Object>();
		for (int i = 0; i < nodes.size(); i++) {
			String column = columns[i];
			String text = nodes.elementAt(i).toPlainTextString();
			if ("日付".equals(column)) {
				map.put("date", DateUtil.parseToDate(text, HTML_DATE_PATTERN));
			} else if ("終値".equals(column)) {
				map.put("close", Double.parseDouble(text));
			} else if ("始値".equals(column)) {
				map.put("open", Double.parseDouble(text));
			} else if ("高値".equals(column)) {
				map.put("high", Double.parseDouble(text));
			} else if ("安値".equals(column)) {
				map.put("low", Double.parseDouble(text));
			}
		}

		return map;
	}

	/**
	 * <p>
	 * 指定した項目名のthタグのNodeFilterを取得する.
	 * </p>
	 * @param column 項目名
	 * @return NodeFilter
	 */
	private static NodeFilter getTableHeaderFilter(String column) {
		List<NodeFilter> filters = new ArrayList<NodeFilter>();
		filters.add(new NodeClassFilter(TableHeader.class));
		filters.add(new HasChildFilter(getTextFilter(column)));
		return new AndFilter(filters.toArray(new NodeFilter[0]));
	}

	/**
	 * <p>
	 * 指定した正規表現のテキストノードのNodeFilterを取得する.
	 * </p>
	 * @param regex 正規表現
	 * @return NodeFilter
	 */
	private static NodeFilter getTextFilter(String regex) {
		List<NodeFilter> filters = new ArrayList<NodeFilter>();
		filters.add(new NodeClassFilter(Text.class));
		filters.add(new RegexFilter(regex));
		return new AndFilter(filters.toArray(new NodeFilter[0]));
	}
}
