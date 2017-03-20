package jp.co.hottolink.buzzfund.crawler;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import jp.co.hottolink.buzzfund.crawler.dao.CrawlResultDao;
import jp.co.hottolink.buzzfund.crawler.dao.RssUrlDao;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * RSSのURLを設定するクラス.
 * </p>
 * @author higa
 */
public class RssUrlSetter {

	/**
	 * <p>
	 * DB接続プロパティファイル名.
	 * </p>
	 */
	private static final String DB_RESOURCE = "buzz_fund_db";

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private static Logger logger = Logger.getLogger(RssUrlSetter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;

		try {
			logger.info("rss url set start");

			// RSSのURLの取得
			executor = new SQLExecutor(DB_RESOURCE);
			RssUrlDao rssUrlDao = new RssUrlDao(executor);
			List<Map<String, String>> records = rssUrlDao.selectAll();

			// RSSのURLの設定
			CrawlResultDao crawlResultDao = new CrawlResultDao(executor);
			for (Map<String, String> record: records) {
				String blogUrl = record.get("blog_url");
				String rssUrl = record.get("rss_url");
				if (crawlResultDao.isExist(blogUrl)) {
					crawlResultDao.updateRss(blogUrl, rssUrl);
				} else {
					crawlResultDao.insertRss(blogUrl, rssUrl);
				}
			}

			logger.info("rss url set end");

		} catch (Exception e) {
			logger.error("RSSのURLの設定に失敗しました", e);
		} finally {
			if (executor != null) executor.finalize();
		}
	}
}
