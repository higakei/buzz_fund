package jp.co.hottolink.buzzfund.calendar.tools;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import jp.co.hottolink.buzzfund.calendar.tools.parser.MarketScheduleHTMLParser;
import jp.co.hottolink.buzzfund.common.velocity.util.VelocityUtil;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;

/**
 * <p>
 * 国内市場のカレンダーを作成するクラス.
 * </p>
 * @author higa
 *
 */
public class CreateMarketCalendar {

	/**
	 * <p>
	 * テンプレートパス.
	 * </p>
	 */
	private static final String TEMPLATE_PATH = "market_calendar.vm";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			// バックナンバーのリンクの取得
			List<String> backNumber = MarketScheduleHTMLParser.getBackNumber();

			// マーケットカレンダーの取得
			Map<Date, Map<String, Object>> calendar = new TreeMap<Date, Map<String,Object>>();
			for (String link: backNumber) {
				if (link.indexOf("200202") > -1) continue;
				calendar.putAll(MarketScheduleHTMLParser.getMonth(link));
			}
			//Map<Date, Map<String, Object>> calendar = MarketScheduleHTMLParser.getMonth(backNumber.get(1));

			// コンテキストに設定
			Collection<Map<String, Object>> list = calendar.values();
			Map<String, Object> context = new HashMap<String, Object>();
			context.put("market_calendar", list);

			// XMLファイルに出力
			String directory = ResourceBundleUtil.getProperty("calendar", "calendar.output.path");
			String file = directory + "market_calendar.xml";
			VelocityUtil.mergeTemplate(TEMPLATE_PATH, context, file);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
