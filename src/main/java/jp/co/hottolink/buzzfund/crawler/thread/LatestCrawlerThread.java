package jp.co.hottolink.buzzfund.crawler.thread;

import java.sql.Connection;

import jp.co.hottolink.buzzfund.crawler.dao.CrawlResultDao;
import jp.co.hottolink.buzzfund.crawler.entity.CrawlResultEntity;
import jp.co.hottolink.buzzfund.crawler.model.ToshiBlogsModel;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

public class LatestCrawlerThread extends Thread {

	/**
	 * <p>
	 * DBコネクション.
	 * </p>
	 */
	private Connection conn = null;

	/**
	 * <p>
	 * クロール情報.
	 * </p>
	 */
	private CrawlResultEntity crawl = null;

	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 * @param conn DBコネクション
	 * @param crawl クロール情報
	 */
	public LatestCrawlerThread(Connection conn, CrawlResultEntity crawl) {
		this.conn = conn;
		this.crawl = crawl;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {

		// クロール結果の初期化
		crawl.setFeedCount(null);
		crawl.setInsertCount(null);

		// クロール
		SQLExecutor executor = new SQLExecutor(conn);
		ToshiBlogsModel model = new ToshiBlogsModel();
		crawl = model.crawl(executor, crawl, true);

		// クロール結果の保存
		CrawlResultDao dao = new CrawlResultDao(executor);
		dao.update(crawl);
	}

	/**
	 * <p>
	 * クロール情報を取得する.
	 * </p>
	 * @return crawl クロール情報
	 */
	public CrawlResultEntity getCrawl() {
		return crawl;
	}
}
