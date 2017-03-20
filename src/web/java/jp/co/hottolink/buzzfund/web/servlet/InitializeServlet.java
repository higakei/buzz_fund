package jp.co.hottolink.buzzfund.web.servlet;

import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.velocity.app.Velocity;

import jp.co.hottolink.buzzfund.web.cache.FibonacciRatioCache;
import jp.co.hottolink.buzzfund.web.cache.PredictionColumnsCache;
import jp.co.hottolink.buzzfund.web.servlet.db.JDBCFactory;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * コンテキストの初期化を行うサーブレット.
 * </p>
 * @author higa
 */
public class InitializeServlet extends HttpServlet {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = -3650878318458136063L;

	/**
	 * <p>
	 * テンプレートパス.
	 * </p>
	 */
	private static final String TEMPLATE_PATH = "/WEB-INF/template";

	/* (non-Javadoc)
	 * @see javax.servlet.GenericServlet#init()
	 */
	@Override
	public void init() throws ServletException {

		SQLExecutor executor = null;

		try {
			// JDBCのデータソースの取得
			JDBCFactory.getInstance();

			// マスターの取得
			executor = JDBCFactory.getExecutor();
			FibonacciRatioCache.initialize(executor);
			PredictionColumnsCache.initialize(executor);

			// Velocityエンジンの初期化
			Properties prop = new Properties();
			String templatePath = getServletContext().getRealPath(TEMPLATE_PATH);
			prop.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH, templatePath);
			prop.setProperty(Velocity.FILE_RESOURCE_LOADER_CACHE, Boolean.TRUE.toString());
			Velocity.init(prop);

		} catch (Exception e) {
			throw new RuntimeException("初期化に失敗しました。", e);
		} finally {
			if (executor != null) executor.finalize();
		}
	}
}
