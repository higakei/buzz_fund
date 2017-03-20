package jp.co.hottolink.buzzfund.crawler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import jp.co.hottolink.buzzfund.crawler.dao.CrawlResultDao;
import jp.co.hottolink.buzzfund.crawler.entity.CrawlResultEntity;
import jp.co.hottolink.buzzfund.crawler.thread.LatestCrawlerThread;
import jp.co.hottolink.splogfilter.common.db.DBConfig;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * 投資分析ブログの最新記事をクロールを行うクラス.
 * </p>
 * @author higa
 */
public class ToshiBlogsLatestCrawler {

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
	private static Logger logger = Logger.getLogger(ToshiBlogsLatestCrawler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Connection conn = null;

		try {
			logger.info("toshi blogs latest crawler start");

			// DBに接続
			conn = DBConfig.getConnection(DB_RESOURCE);

			// 最新記事をクロールするブログの取得
			SQLExecutor executor = new SQLExecutor(conn);
			CrawlResultDao dao = new CrawlResultDao(executor);
			List<CrawlResultEntity> crawlBlogs = dao.selectForLatest();
			logger.info("crawl blog:" + crawlBlogs.size());

			int insertCount = 0;
			List<LatestCrawlerThread> threads = new ArrayList<LatestCrawlerThread>();
			for (CrawlResultEntity crawl: crawlBlogs) {
				LatestCrawlerThread thread = new LatestCrawlerThread(conn, crawl);
				thread.start();
				thread.join(100);
				if (thread.isAlive()) {
					threads.add(thread);
				} else {
					insertCount += getInsertCount(thread.getCrawl());
				}
			}

			// クロール結果の保存
			logger.info("alive thread:" + threads.size());
			for (LatestCrawlerThread thread: threads) {
				thread.join();
				insertCount += getInsertCount(thread.getCrawl());
			}

			logger.info("toshi blogs latest crawler end(" + insertCount + ")");
			System.out.println("正常終了");
			System.exit(0);

		} catch (Exception e) {
			logger.error("クロールに失敗しました。", e);
			e.printStackTrace(System.err);
			System.exit(1);
		} finally {
			if (conn != null) try { conn.close(); } catch (SQLException e) {}
		}
	}

	/**
	 * <p>
	 * クロール結果からクロール数を取得する.
	 * </p>
	 * @param result クロール結果
	 * @return クロール数
	 */
	private static int getInsertCount(CrawlResultEntity result) {

		// ログ出力
		if (result.getFeedCount() == null) {
			logger.info(result.getBlogUrl() + "\t" + "feed failed");
		} else {
			logger.info(result.getBlogUrl()
					+ "\t" + "feed=" + result.getFeedCount()
					+ "\t" + "insert=" + result.getInsertCount());
		}

		// クロール数を返す
		Integer insertCount = result.getInsertCount();
		if (insertCount == null) {
			return 0;
		} else {
			return insertCount;
		}
	}
}
