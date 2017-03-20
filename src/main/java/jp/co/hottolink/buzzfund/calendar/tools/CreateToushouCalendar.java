package jp.co.hottolink.buzzfund.calendar.tools;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.hottolink.buzzfund.calendar.tools.parser.ToushouCalendarHTMLParser;
import jp.co.hottolink.buzzfund.common.velocity.util.VelocityUtil;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;

/**
 * <p>
 * 東証カレンダーを作成するクラス.
 * </p>
 * @author higa
 */
public class CreateToushouCalendar {

	/**
	 * <p>
	 * テンプレートパス.
	 * </p>
	 */
	private static final String TEMPLATE_PATH = "toushou_calendar.vm";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// 選択月リストの取得
			ToushouCalendarHTMLParser parser = new ToushouCalendarHTMLParser();
			List<String> selMonths = parser.getSelMonths();
			String firstMonth = selMonths.get(0);
			String lastMonth = selMonths.get(selMonths.size() - 1);

			// 東証カレンダーの取得
			Map<Date, Map<String, Object>> calendar = new TreeMap<Date, Map<String,Object>>();
			for (String selMonth: selMonths) {
				calendar.putAll(parser.getMonth(firstMonth, lastMonth, selMonth));
			}

			// コンテキストに設定
			Collection<Map<String, Object>> list = calendar.values();
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("toushou_calendar", list);

			// XMLファイルに出力
			String directory = ResourceBundleUtil.getProperty("calendar", "calendar.output.path");
			String file = directory + "toushou_calendar.xml";
			VelocityUtil.mergeTemplate(TEMPLATE_PATH, context, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
