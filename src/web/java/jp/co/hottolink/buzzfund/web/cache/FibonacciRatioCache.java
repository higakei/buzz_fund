package jp.co.hottolink.buzzfund.web.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.web.dao.FibonacciRatioDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * フィボナッチ比率マスターのキャッシュクラス.
 * </p>
 * @author higa
 */
public class FibonacciRatioCache {

	/**
	 * <p>
	 * 昇順マスターのキャッシュ.
	 * </p>
	 */
	private static List<Map<String, Object>> ascmaxCahche = new ArrayList<Map<String,Object>>();

	/**
	 * <p>
	 * 降順マスターのキャッシュ.
	 * </p>
	 */
	private static List<Map<String, Object>> descCahche = new ArrayList<Map<String,Object>>();

	/**
	 * <p>
	 * キャッシュを初期化する.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public static void initialize(SQLExecutor executor) {
		synchronized (ascmaxCahche) { ascmaxCahche = getAsc(executor); };
		synchronized (descCahche) { descCahche = getDesc(executor); };
	}

	/**
	 * <p>
	 * キャッシュをクリアする.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public static void clear() {
		synchronized (ascmaxCahche) { ascmaxCahche.clear(); };
		synchronized (descCahche) { descCahche.clear(); };
	}

	/**
	 * <p>
	 * 昇順マスターのキャッシュを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 昇順マスター
	 */
	public static synchronized List<Map<String, Object>> getAscCache(SQLExecutor executor) {
		if ((ascmaxCahche == null) || ascmaxCahche.isEmpty()) {
			ascmaxCahche = getAsc(executor);
			return ascmaxCahche;
		} else {
			return ascmaxCahche;
		}
	}

	/**
	 * <p>
	 * 降順マスターのキャッシュを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 降順マスター
	 */
	public static synchronized List<Map<String, Object>> getDescCache(SQLExecutor executor) {
		if ((descCahche == null) || descCahche.isEmpty()) {
			descCahche = getDesc(executor);
			return descCahche;
		} else {
			return descCahche;
		}
	}

	/**
	 * <p>
	 * 昇順マスターを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 昇順マスター
	 */
	private static List<Map<String, Object>> getAsc(SQLExecutor executor) {
		FibonacciRatioDao dao = new FibonacciRatioDao(executor);
		return dao.selectInRatioAsc();
	}

	/**
	 * <p>
	 * 降順マスターを取得する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @return 降順マスター
	 */
	private static List<Map<String, Object>> getDesc(SQLExecutor executor) {
		FibonacciRatioDao dao = new FibonacciRatioDao(executor);
		return dao.selectInRatioDesc();
	}
}
