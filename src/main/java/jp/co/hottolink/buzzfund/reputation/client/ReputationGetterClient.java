package jp.co.hottolink.buzzfund.reputation.client;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import jp.co.hottolink.buzzfund.reputation.ReputationGetter;
import jp.co.hottolink.buzzfund.reputation.entity.ReputationCountEntity;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.exception.APIException;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 評判情報取得を実行するクラス.
 * </p>
 * @author higa
 */

public class ReputationGetterClient {

	/**
	 * <p>
	 * APIで指定できる最大期間.
	 * </p>
	 */
	private static final int MAX_API_RANGE = 30;

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private static Logger logger = Logger.getLogger(ReputationGetterClient.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;
		String from = null;
		String to = null;
		String day = null;

		try {
			logger.info("reputation getter start");

			// パラメータの取得
			for (String arg: args) {
				String[] splits = arg.split("=",2);
				if (splits.length < 2) {
					continue;
				}

				if (splits[0].startsWith("--from")) {
					from = splits[1];
				} else if (splits[0].startsWith("--to")) {
					to = splits[1];
				} else if (splits[0].startsWith("--day")) {
					day = splits[1];
				}
			}

			// 開始日のバリデーション
			if (from != null) {
				if (!DateUtil.validateDate(from)) {
					String message = "from(" + from + ") is invalid date.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}

			// 終了日のバリデーション
			if (to != null) {
				if (!DateUtil.validateDate(to)) {
					String message = "to(" + to + ") is invalid date.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}

			// 日数のバリデーション
			if (day != null) {
				if (!validateNumber(day)) {
					String message = "day(" + day + ") is invalid number.";
					logger.info(message);
					System.err.println(message);
					System.exit(1);
				}
			}

			// 
			if ((from != null) && (to != null) && (day == null)) {
			} else if ((from == null) && (to == null) && (day != null)) {
			} else {
				String message = "parameter(" + getParameters(args) + ") is invalid.";
				logger.info(message);
				System.err.println(message);
				System.err.println("--from=YYYY-MM-DD --to=YYYY-MM-DD or --day=NUMBER");
				System.exit(1);
			}

			// Buzz APIの検索期間の取得
			List<int[]> ranges = null;
			if ((from != null) && (to != null) && (day == null)) {
				ranges = getRanges(from, to);
			} else if ((from == null) && (to == null) && (day != null)) {
				ranges = getRanges(day);
			}

			// 評判検索語の取得
			executor = new SQLExecutor("buzz_fund_db");
			List<String> words = getWords(executor);

			// 評判情報の取得
			for (int[] range: ranges) {
				Date fromDate = DateUtil.getOffsetDate(range[0]);
				Date toDate = DateUtil.getOffsetDate(range[1]);
				for (String word: words) {
					logger.info("search_word=" + word
							+ ", from=" + DateUtil.toString(fromDate, "yyyy-MM-dd")
							+ ", to=" + DateUtil.toString(toDate, "yyyy-MM-dd")
							);
					try {
						ReputationGetter getter = new ReputationGetter(executor);
						List<ReputationCountEntity> counts = getter.getCounts(word, fromDate, toDate);
						getter.setCounts(counts);
					} catch (APIException e) {
						String detail = getDetail(word, fromDate, toDate);
						System.out.println(detail + ", " + e);
						logger.error(detail, e);
					}
				}
			}

			logger.info("reputation getter end");
			System.exit(0);

		} catch (Exception e) {
			logger.error("failed", e);
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * 評判検索語を取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 評判検索語
	 * @throws SQLException
	 */
	private static List<String> getWords(SQLExecutor executor) throws SQLException {

		try {
			// SQLの実行
			executor.preparedStatement("select word from reputation_word");
			executor.executeQuery();

			// 評判検索語の取得
			List<String> words = new ArrayList<String>();
			while (executor.next()) {
				words.add(executor.getString("word"));
			}

			return words;

		} finally {
			executor.closeQuery();
		}
	}

	/**
	 * <p>
	 * Buzz APIで検索する期間を取得する.
	 * </p>
	 * @param from 開始日
	 * @param to 終了日
	 * @return 期間
	 */
	private static List<int[]> getRanges(Date from, Date to) {

		logger.info("from=" + DateUtil.toString(from, "yyyy/MM/dd")
				+ ", to=" + DateUtil.toString(to, "yyyy/MM/dd")
				);

		List<int[]> ranges = new ArrayList<int[]>();
		Integer formDay = DateUtil.getOffsetDay(from);
		Integer toDay = DateUtil.getOffsetDay(to);

		for (;;) {
			if ((toDay - formDay) < MAX_API_RANGE) {
				int[] range = new int[2];
				range[0] = formDay;
				range[1] = toDay;
				ranges.add(range);
				return ranges;
			}

			int[] range = new int[2];
			range[0] = formDay;
			range[1] = formDay + MAX_API_RANGE - 1;
			ranges.add(range);
			formDay = formDay + MAX_API_RANGE;
		}
	}

	/**
	 * <p>
	 * 数字文字列のバリデートを行う.
	 * </p>
	 * @param number 数字文字
	 * @return <code>true</code>:有効, <code>false</code>:無効
	 */
	private static boolean validateNumber(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * <p>
	 * Buzz APIで検索する期間を取得する.
	 * </p>
	 * @param from 開始日
	 * @param to 終了日
	 * @return 期間
	 * @throws ParseException 
	 */
	private static List<int[]> getRanges(String from, String to) throws ParseException {
		Date fromDate = DateUtil.parseToDate(from);
		Date toDate = DateUtil.parseToDate(to);
		if (fromDate.after(toDate)) {
			return getRanges(toDate, fromDate);
		} else {
			return getRanges(fromDate, toDate);
		}
	}

	/**
	 * Buzz APIで検索する期間を取得する.
	 * </p><pre>
	 * 指定した日数の現在日付から過去の期間
	 * ※現在日付は含まない
	 * <pre>
	 * @param day 日数
	 * @return 期間
	 */
	private static List<int[]> getRanges(String day) {

		// 現在日付の取得
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);

		// 開始日
		Calendar tmp = (Calendar)calendar.clone();
		tmp.add(Calendar.DATE, - Integer.parseInt(day));
		Date from = tmp.getTime();

		// 終了日
		tmp = (Calendar)calendar.clone();
		tmp.add(Calendar.DATE, -1);
		Date to = tmp.getTime();

		return getRanges(from, to);
	}

	/**
	 * <p>
	 * 詳細情報を作成する.
	 * </p>
	 * @param searchWord 検索語
	 * @param from 開始日
	 * @param to 終了日
	 * @return 詳細情報
	 */
	private static String getDetail(String searchWord, Date from, Date to) {
		StringBuffer buffer = new StringBuffer();
		buffer.append("search_word=" + searchWord);
		buffer.append(", ");
		buffer.append("from=" + DateUtil.toString(from, "yyyy-MM-dd"));
		buffer.append(", ");
		buffer.append("to=" + DateUtil.toString(to, "yyyy-MM-dd"));
		return buffer.toString();
	}

	/**
	 * <p>
	 * パラメータ情報のを作成する.
	 * </p>
	 * @param parameters パラメータ
	 * @return 表示文字列
	 */
	private static String getParameters(String[] parameters) {

		StringBuffer buffer = new StringBuffer();
		if ((parameters == null) || (parameters.length == 0)) {
			return buffer.toString();
		}

		for (String parameter: parameters) {
			buffer.append(" " + parameter);
		}

		return buffer.substring(1);
	}
}
