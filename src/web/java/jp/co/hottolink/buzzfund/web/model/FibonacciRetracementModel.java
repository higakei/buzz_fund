package jp.co.hottolink.buzzfund.web.model;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.web.cache.FibonacciRatioCache;
import jp.co.hottolink.buzzfund.web.dao.PriceDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * フィボナッチリトレースメントのModelクラス.
 * </p>
 * @author higa
 */
public class FibonacciRetracementModel {

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
	public FibonacciRetracementModel(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * フィボナッチリトレースメントを取得する.
	 * </p>
	 * @param period 予測期間
	 * @return フィボナッチリトレースメント
	 */
	public Map<String, Object> get(Map<String, Date> period) {

		Date from = period.get("from");
		Date to = period.get("to");

		// 最高値と最安値の取得
		PriceDao dao = new PriceDao(executor);
		Map<String, Integer> price = dao.selectMaxMin(from, to);
		if (price == null) {
			return null;
		}

		Integer max = price.get("max");
		Integer min = price.get("min");
		// 高値から安値の下げ幅に対する戻り
		List<Map<String, Object>> highToLow = getHighToLow(max, min);
		// 安値から高値の上げ幅に対する押し
		List<Map<String, Object>> lowToHigh = getLowToHigh(max, min);

		Map<String, Object> retracement = new HashMap<String, Object>();
		retracement.putAll(period);
		retracement.putAll(price);
		retracement.put("highToLow", highToLow);
		retracement.put("lowToHigh", lowToHigh);

		return retracement;
	}

	/**
	 * <p>
	 * フィボナッチリトレースメントを取得する
	 * </p>
	 * @param from 開始日
	 * @param to 終了日
	 * @return フィボナッチリトレースメント
	 * @throws ParseException 
	 * 
	 */
	public Map<String, Object> get(Date from, Date to) throws ParseException {

		// 最高値と最安値の取得
		PriceDao dao = new PriceDao(executor);
		Map<String, Integer> price = dao.selectMaxMin(from, to);
		if (price == null) {
			return null;
		}

		Integer max = price.get("max");
		Integer min = price.get("min");
		// 高値から安値の下げ幅に対する戻り
		List<Map<String, Object>> highToLow = getHighToLow(max, min);
		// 安値から高値の上げ幅に対する押し
		List<Map<String, Object>> lowToHigh = getLowToHigh(max, min);

		Map<String, Object> retracement = new HashMap<String, Object>();
		retracement.put("from", from);
		retracement.put("to", to);
		retracement.putAll(price);
		retracement.put("highToLow", highToLow);
		retracement.put("lowToHigh", lowToHigh);

		return retracement;
	}

	/**
	 * <p>
	 * 高値から安値の下げ幅に対する戻りを取得する.
	 * </p>
	 * @param max 最高値
	 * @param min 最安値
	 * @return 高値から安値の下げ幅に対する戻り
	 */
	private List<Map<String, Object>> getHighToLow(int max, int min) {

		// フィボナッチ比率の取得
		List<Map<String, Object>> highToLow = FibonacciRatioCache.getAscCache(executor);

		// 高値から安値
		for (Map<String, Object> map: highToLow) {
			double ratio = (Double)map.get("ratio");
			int retracement = ((int)((max - ratio * (max - min)) / 5)) * 5;
			map.put("retracement", retracement);
		}

		return highToLow;
	}

	/**
	 * <p>
	 * 安値から高値の上げ幅に対する押しを取得する.
	 * </p>
	 * @param max 最高値
	 * @param min 最安値
	 * @return 安値から高値の上げ幅に対する押し
	 */
	private List<Map<String, Object>> getLowToHigh(int max, int min) {

		// フィボナッチ比率の取得
		List<Map<String, Object>> lowToHigh = FibonacciRatioCache.getDescCache(executor);

		// 安値から高値
		for (Map<String, Object> map: lowToHigh) {
			double ratio = (Double)map.get("ratio");
			int retracement = ((int)((min + ratio * (max - min)) / 5)) * 5;
			map.put("retracement", retracement);
		}

		return lowToHigh;
	}
}
