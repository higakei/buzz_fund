package jp.co.hottolink.buzzfund.web.model;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import jp.co.hottolink.buzzfund.web.cache.PredictionColumnsCache;
import jp.co.hottolink.buzzfund.web.dao.PredictionLogDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * 株価指数のモデルクラス.
 * </p>
 * @author higa
 */
public class PredicitionModel {

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
	public PredicitionModel(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 指定した日の株価指数を取得する.
	 * </p>
	 * @param date 日付
	 * @return 指定した日
	 * @throws ParseException 
	 */
	public Map<String, Object> get(String date) throws ParseException {

		// 指定した日のデータの取得
		PredictionLogDao dao = new PredictionLogDao(executor);
		Map<String, Object> prediction  = dao.selectByPkey(date);
		if (prediction == null) {
			return null;
		}

		// 画面表示情報の追加
		add(prediction);
		return prediction;
	}

	/**
	 * <p>
	 * 指定した日から最新の株価指数を取得する.
	 * </p>
	 * @param date 日付
	 * @return 株価指数
	 */
	public Map<String, Object> getLatest(Date date) {

		// 指定した日のデータの取得
		PredictionLogDao dao = new PredictionLogDao(executor);
		Map<String, Object> prediction = dao.selectLatest(date);
		if (prediction == null) {
			return null;
		}

		// 画面表示情報の追加
		add(prediction);
		return prediction;
	}

	/**
	 * <p>
	 * 画面表示情報を追加する.
	 * </p>
	 * @param prediction 株価指数情報
	 */
	private void add(Map<String, Object> prediction) {

		if (prediction == null) {
			return;
		}

		// 画面表示項目の取得
		Map<String, Map<String, Object>> visible = PredictionColumnsCache.getVisibleCache(executor);
		if (visible == null) {
			return;
		}

		for (String key: visible.keySet()) {
			Map<String, Object> column = visible.get(key);
			String label = (String)column.get("label");
			String up = (String)column.get("up");
			String down = (String)column.get("down");
			if ((up == null) || (down == null)) {
				continue;
			}

			String upDown = (String)prediction.get(key);
			if ("up".equals(upDown)) {
				prediction.put(label, up);
			} else if ("down".equals(upDown)) {
				prediction.put(label, down);
			}
		}
	}
}
