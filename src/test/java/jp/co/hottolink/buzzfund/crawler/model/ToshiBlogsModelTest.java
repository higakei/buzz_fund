package jp.co.hottolink.buzzfund.crawler.model;

import java.io.IOException;

import org.htmlparser.util.ParserException;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * ToshiBlogsModelのテストクラス.
 * </p>
 * @author higa
 */
public class ToshiBlogsModelTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;

		try {
			//executor = new SQLExecutor("buzz_fund_db");
			//executor.executeQuery("select * from crawl_result");

			//while (executor.next()) {
			//	String blogUrl = executor.getString("blog_url");
			for (String blogUrl: args) {
				try {
					String rssUrl = getRssUrlFromHtml(blogUrl);
					System.out.println(blogUrl + "\t" + rssUrl);
				} catch (Exception e) {
					continue;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	public static String getRssUrlFromHtml(String blogUrl) throws ParserException, IOException {
		ToshiBlogsModel model = new ToshiBlogsModel();
		return model.getRssUrlFromHtml(blogUrl);
	}
}
