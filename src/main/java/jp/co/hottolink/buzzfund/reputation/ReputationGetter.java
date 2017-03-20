package jp.co.hottolink.buzzfund.reputation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.reputation.dao.ReputationCountDao;
import jp.co.hottolink.buzzfund.reputation.entity.ReputationCountEntity;
import jp.co.hottolink.splogfilter.common.api.buzz.ReputationCountAPI;
import jp.co.hottolink.splogfilter.common.api.buzz.constants.BuzzAPIConstants;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 評判情報をBuzz APIから取得するクラス.
 * </p>
 * @author higa
 */
public class ReputationGetter {

	/**
	 * <p>
	 * デフォルトのsplogフィルタ.
	 * </p>
	 */
	private static int DEFAULT_SPLOG = BuzzAPIConstants.SPLOG_MEDIUM + BuzzAPIConstants.SPAM_BODY_DEPLICATED;

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
	 */
	public ReputationGetter() {}

	
	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 * @param executor SQL実行クラス
	 */
	public ReputationGetter(SQLExecutor executor) {
		this.executor = executor;
	}

	/**
	 * <p>
	 * 評判情報をBuzz APIから取得する.
	 * </p>
	 * @param word
	 * @param from 開始日
	 * @param to 終了日
	 * @return 評判情報
	 * @throws ParseException 
	 */
	public List<ReputationCountEntity> getCounts(String word, Date from, Date to) throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat(BuzzAPIConstants.DATE_FORMAT);
		return getCounts(word, formatter.format(from), formatter.format(to));
	}

	/**
	 * <p>
	 * 評判情報をBuzz APIから取得する.
	 * </p>
	 * @param word
	 * @param from 開始日
	 * @param to 終了日
	 * @return 評判情報
	 * @throws ParseException 
	 */
	public List<ReputationCountEntity> getCounts(String word, String from, String to) throws ParseException {

		// APIに問い合わせ
		ReputationCountAPI api = new ReputationCountAPI();
		api.setDomain(BuzzAPIConstants.DOMAIN_BLOG);
		api.setSplog(DEFAULT_SPLOG);
		List<Map<String, String>> list = api.call(word, from, to);

		// 結果を格納
		List<ReputationCountEntity> counts = new ArrayList<ReputationCountEntity>();
		for (Map<String, String> map: list) {
			ReputationCountEntity count = new ReputationCountEntity();
			count.setDate(DateUtil.parseToDate(map.get("fromDate"), BuzzAPIConstants.DATE_FORMAT));
			count.setWord(word);
			count.setSplog(DEFAULT_SPLOG);
			count.setPositive(Integer.parseInt(map.get("positive")));
			count.setFlat(Integer.parseInt(map.get("flat")));
			count.setNegative(Integer.parseInt(map.get("negative")));
			count.setTotal(Integer.parseInt(map.get("all")));
			count.setResultStatus(api.getResultStatus());
			counts.add(count);
		}

		return counts;
	}

	/**
	 * <p>
	 * 評判情報をDBに保存する.
	 * </p>
	 * @param counts 評判情報
	 */
	public void setCounts(List<ReputationCountEntity> counts) {

		if (counts == null) {
			return;
		}

		ReputationCountDao dao = new ReputationCountDao(executor);
		for (ReputationCountEntity count: counts) {
			if (!dao.isDuplicate(count)) {
				dao.insert(count);
			}
		}
	}
}
