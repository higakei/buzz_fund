package jp.co.hottolink.buzzfund.web.cache;

import java.util.HashMap;
import java.util.Map;

import jp.co.hottolink.buzzfund.web.dao.PredicitonColumnsDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * prediction_columnsのテーブルのキャッシュクラス.
 * </p>
 * @author higa
 */
public class PredictionColumnsCache {

	/**
	 * <p>
	 * 最大キャッシュ時間.
	 * </p>
	 */
	private static final long MAX_CACHED_TIME = 60 * 60 * 1000;

	/**
	 * <p>
	 * キャッシュ開始時間.
	 * </p>
	 */
	private static long time = 0;

	/**
	 * <p>
	 * テーブルのキャッシュ.
	 * </p>
	 */
	private static Map<String, Map<String, Object>> visible = new HashMap<String, Map<String,Object>>();

	/**
	 * <p>
	 * キャッシュを初期化する.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public static void initialize(SQLExecutor executor) {
		synchronized (visible) { visible = getVisible(executor); };
	}

	/**
	 * <p>
	 * キャッシュをクリアする.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public static void clear() {
		synchronized (visible) { visible.clear(); };
	}

	/**
	 * <p>
	 * 昇順マスターのキャッシュを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 昇順マスター
	 */
	public static synchronized Map<String, Map<String, Object>> getVisibleCache(SQLExecutor executor) {
		if ((visible == null) || visible.isEmpty()) {
			visible = getVisible(executor);
			time = System.currentTimeMillis();
			return visible;
		} else {
			long current = System.currentTimeMillis();
			if ((current - time) > MAX_CACHED_TIME) {
				visible = getVisible(executor);
				time = System.currentTimeMillis();
				return visible;
			} else {
				return visible;
			}
		}
	}

	/**
	 * <p>
	 * 画面表示カラムを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 画面表示カラム
	 */
	private static Map<String, Map<String, Object>> getVisible(SQLExecutor executor) {
		PredicitonColumnsDao dao = new PredicitonColumnsDao(executor);
		return dao.selectVisible();
	}
}
