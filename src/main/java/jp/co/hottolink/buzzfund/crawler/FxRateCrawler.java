package jp.co.hottolink.buzzfund.crawler;

import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.crawler.dao.FxRateDao;
import jp.co.hottolink.buzzfund.crawler.parser.FxRateParser;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

import org.apache.log4j.Logger;

/**
 * <p>
 * 為替レートのクロールを行うクラス.
 * </p>
 * @author higa
 */
public class FxRateCrawler {

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private static Logger logger = Logger.getLogger(FxRateCrawler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;
		String sFrom = null;
		String sTo = null;
		String currency = null;

		try {
			logger.info("fx rate crawler start");
	
			// パラメータの取得
			for (String arg: args) {
				String[] splits = arg.split("=", 2);
				if (splits.length < 2) {
					continue;
				}
	
				if (splits[0].startsWith("--from")) {
					sFrom = splits[1];
				} else if (splits[0].startsWith("--to")) {
					sTo = splits[1];
				} else if (splits[0].startsWith("--currency")) {
					currency = splits[1];
				}
			}
	
			// 開始日のバリデーション
			if (sFrom != null) {
				if (!DateUtil.validateDate(sFrom)) {
					String message = "from(" + sFrom + ") is invalid date.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}
	
			// 終了日のバリデーション
			if (sTo != null) {
				if (!DateUtil.validateDate(sTo)) {
					String message = "to(" + sTo + ") is invalid date.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}

			// 通貨のバリデーション
			if (currency == null) {
				String message = "currency is required.";
				logger.info(message);
				System.err.println(message);
				System.exit(1);
			} else {
				try {
					ResourceBundleUtil.getProperty("infoseek", currency);
				} catch (Exception e) {
					String message = "currency(" + currency + ") is invalid.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}

			// 為替レートの取得
			Date from = null;
			if (sFrom != null) from = DateUtil.parseToDate(sFrom);
			Date  to = null;
			if (sTo != null) to = DateUtil.parseToDate(sTo);
			List<Map<String, Object>> rates = FxRateParser.getRates(currency, from, to);

			// DBに保存
			executor = new SQLExecutor("buzz_fund_db");
			for (Map<String, Object> rate: rates) {
				FxRateDao dao = new FxRateDao(executor);
				if (!dao.isDuplicate(rate)) {
					dao.insert(rate);
					logger.info("currency=" + rate.get("currency") + "\t" + "date="
							+ DateUtil.toString((Date)rate.get("date"), "yyyy-MM-dd"));
				}
			}

			logger.info("fx rate crawler end");
			System.exit(0);

		} catch (Exception e) {
			logger.error("fx rate crawler failed", e);
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if (executor != null) executor.finalize();
		}
	}

}
