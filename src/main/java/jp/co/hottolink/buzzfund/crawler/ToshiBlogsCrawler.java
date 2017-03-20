package jp.co.hottolink.buzzfund.crawler;

import java.util.List;

import jp.co.hottolink.buzzfund.crawler.entity.CrawlResultEntity;
import jp.co.hottolink.buzzfund.crawler.model.ToshiBlogsModel;

import org.apache.log4j.Logger;

/**
 * <p>
 * 投資分析ブログのクロールを行うクラス.
 * </p>
 * @author higa
 */
public class ToshiBlogsCrawler {

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private static Logger logger = Logger.getLogger(ToshiBlogsCrawler.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			logger.info("toshi blogs crawler start");

			// クロールするURLの取得
			ToshiBlogsModel model = new ToshiBlogsModel();
			List<String> crawlUrls = model.getCrawlUrls();

			for (String crawlUrl: crawlUrls) {
				// クロール結果の取得
				CrawlResultEntity result = model.getResult(crawlUrl);
				if (result == null) {
					result = new CrawlResultEntity();
					result.setBlogUrl(crawlUrl);
				}

				// クロール結果の初期化
				result.setFeedCount(null);
				result.setInsertCount(null);

				// RSSのURLの取得
				String rssUrl = model.getRssUrl(result);
				if (rssUrl == null) {
					model.setResult(result);
					logger.info(crawlUrl + "\t" + "rss url not found");
					continue;
				}

				// クロール
				result = model.crawl(result, false);
				if (result.getFeedCount() == null) {
					model.setResult(result);
					logger.info(crawlUrl + "\t" + "feed failed");
					continue;
				}

				// クロール結果の保存
				model.setResult(result);
				logger.info(crawlUrl
						+ "\t" + "feed=" + result.getFeedCount()
						+ "\t" + "insert=" + result.getInsertCount());
			}

			logger.info("toshi blogs crawler end");
			System.out.println("正常終了");
			System.exit(0);

		} catch (Exception e) {
			logger.error("クロールに失敗しました。", e);
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}
}
