package jp.co.hottolink.buzzfund.crawler;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.co.hottolink.buzzfund.crawler.model.ToshiBlogsModel;
import jp.co.hottolink.buzzfund.crawler.util.AuthorIdGenerator;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.resource.ResourceBundleUtil;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * ブログフィードを削除するクラス.
 * </p>
 * @author higa
 */
public class BlogFeedDeleter {

	/**
	 * <p>
	 * DB接続プロパティファイル名.
	 * </p>
	 */
	private static final String DB_RESOURCE = "buzz_fund_db";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			// 指定日の設定
			int month = ResourceBundleUtil.getIntProperty("crawler", "feed.save.month");
			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MONTH, -month);
			calendar.set(Calendar.DATE, calendar.getActualMinimum(Calendar.DATE));
			Date date = new Date(calendar.getTimeInMillis());

			// リスト作成
			List<Map<String, String>> noDate = getNoDateBlogList();
			List<Map<String, String>> notCrawled = getNotCrawled();

			// ブログフィードの削除
			int rows = delete(date);

			// 集計
			setCrawlResult();

			// 結果出力
			System.out.println(date +"以前のブログフィードを" + rows + "件削除しました。");

			// 日付なし
			if (!noDate.isEmpty()) {
				System.out.println("");
				System.out.println("日付がないフィードのブログ");
				System.out.println("blog_url" + "," + "authorId" + "," + "crawl_count");
			}
			for (Map<String, String> map: noDate) {
				System.out.println(map.get("blog_url")
						+ "," + map.get("authorId")
						+ "," + map.get("crawl_count"));
			}

			// クロールしていない
			if (!notCrawled.isEmpty()) {
				System.out.println("");
				System.out.println("クロールしていないブログ");
				System.out.println("blog_url" + "," + "latest_blog_date" + "," + "rss_url");
			}
			for (Map<String, String> map: notCrawled) {
				System.out.println(map.get("blog_url")
						+ "," + map.get("latest_blog_date")
						+ "," + map.get("rss_url"));
			}

			System.exit(0);

		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(1);
		}
	}

	/**
	 * <p>
	 * 日付がないフィードのブログリストを取得する.
	 * </p>
	 * @return ブログリスト
	 * @throws SQLException
	 * @throws NoSuchAlgorithmException
	 * @throws MalformedURLException
	 * @throws URISyntaxException
	 */
	private static List<Map<String, String>> getNoDateBlogList()
			throws SQLException, NoSuchAlgorithmException,
			MalformedURLException, URISyntaxException {

		SQLExecutor executor = null;

		try {
			// SQLの実行
			executor = new SQLExecutor(DB_RESOURCE);
			executor.executeQuery("select * from crawl_result where total_crawl_count > 0 and latest_blog_date is null");

			// リストの作成
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			while (executor.next()) {
				// データの取得
				String blogUrl = executor.getString("blog_url");
				Integer total = executor.getInt("total_crawl_count");
				String authorId = AuthorIdGenerator.generate(blogUrl);

				// リストに追加
				Map<String, String> map = new HashMap<String, String>();
				map.put("blog_url", blogUrl);
				map.put("authorId", authorId);
				map.put("crawl_count", total.toString());

				list.add(map);
			}

			return list;

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * クロールしていないブログリストを取得する.
	 * </p>
	 * @return ブログリスト
	 * @throws SQLException
	 */
	private static List<Map<String, String>> getNotCrawled() throws SQLException {

		SQLExecutor executor = null;

		try {
			// SQLの実行
			executor = new SQLExecutor(DB_RESOURCE);
			executor.executeQuery("select * from crawl_result where total_crawl_count = 0");

			// リストの作成
			List<Map<String, String>> list = new ArrayList<Map<String,String>>();
			while (executor.next()) {
				// データの取得
				String blogUrl = executor.getString("blog_url");
				String rssUrl = executor.getString("rss_url");
				Timestamp latest = executor.getTimestamp("latest_blog_date");

				// RSSのURLの取得
				if (rssUrl == null) {
					try {
						ToshiBlogsModel model = new ToshiBlogsModel();
						rssUrl = model.getRssUrlFromHtml(blogUrl);
					} catch (Exception e) {}
				}

				// リストに追加
				Map<String, String> map = new HashMap<String, String>();
				map.put("blog_url", blogUrl);
				map.put("latest_blog_date", (latest == null) ? "-":DateUtil.toString(latest, "yyyy-MM-DD HH:mm:ss"));
				map.put("rss_url", (rssUrl == null) ? "rss url not found":rssUrl);
				list.add(map);
			}

			return list;

		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * 指定日より古いブログフィードを削除する.
	 * </p>
	 * @param date 指定日
	 * @return 削除件数
	 * @throws SQLException
	 */
	private static int delete(Date date) throws SQLException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			executor.preparedStatement("delete from blogfeedRDF where date < ? or (date is null and creation_date < ?)");
			executor.setDate(1, date);
			executor.setDate(2, date);
			return executor.executeUpdate();
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * クロール結果を集計する.
	 * </p>
	 * @throws SQLException 
	 * @throws URISyntaxException 
	 * @throws MalformedURLException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static void setCrawlResult() throws SQLException,
			NoSuchAlgorithmException, MalformedURLException, URISyntaxException {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor(DB_RESOURCE);
			executor.executeQuery("select * from crawl_result");

			List<String> urls = new ArrayList<String>();
			while (executor.next()) {
				urls.add(executor.getString("blog_url"));
			}

			for (String blogUrl: urls) {
				String authorId = AuthorIdGenerator.generate(blogUrl);
				executor.preparedStatement("select count(*) as count, max(date) as latest from blogfeedRDF where authorId = ?");
				executor.setString(1, authorId);
				executor.executeQuery();
				executor.next();
				int count = executor.getInt("count");
				Timestamp latest = executor.getTimestamp("latest");

				executor.preparedStatement("update crawl_result set total_crawl_count = ?, latest_blog_date = ? where blog_url = ?");
				executor.setInt(1, count);
				executor.setTimestamp(2, latest);
				executor.setString(3, blogUrl);
				executor.executeUpdate();
			}

		} finally {
			if (executor != null) executor.finalize();
		}
	}
}
