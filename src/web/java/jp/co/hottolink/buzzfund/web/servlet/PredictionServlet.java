package jp.co.hottolink.buzzfund.web.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import jp.co.hottolink.buzzfund.calendar.util.ToushiCalendarUtil;
import jp.co.hottolink.buzzfund.web.model.PredicitionModel;
import jp.co.hottolink.buzzfund.web.servlet.db.JDBCFactory;
import jp.co.hottolink.buzzfund.web.servlet.util.ServletUtil;
import jp.co.hottolink.buzzfund.web.velocity.VelocityContextFactory;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * 株価指数を取得するサーブレットクラス.
 * </p>
 * @author higa
 */
public class PredictionServlet extends HttpServlet {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = 1324162540034839978L;

	/**
	 * <p>
	 * テンプレートパス.
	 * </p>
	 */
	private static final String TEMPLATE_PATH = "prediction.vm";

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private Logger logger = Logger.getLogger(PredictionServlet.class);

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		SQLExecutor executor = null;

		try {
			// パラメータの取得
			String date = request.getParameter("date");
	
			// バリデーション
			executor = JDBCFactory.getExecutor();
			if (!DateUtil.validate(date)) {
				ServletUtil.outputPlain(response, "日付の入力が正しくありません");
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
			} else if (!ToushiCalendarUtil.isOpen(executor, DateUtil.parseToDate(date))) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "休業日です");
				return;
			}
	
			// 株価指数の取得
			PredicitionModel pModel = new PredicitionModel(executor);
			Map<String, Object> prediction = pModel.get(date);
			if (prediction == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "指定した日付のデータはありません");
				return;
			}

			// テキスト出力
			VelocityContext context = VelocityContextFactory.getInstance();
			context.put("prediction", prediction);
			ServletUtil.outputPlain(response, context, TEMPLATE_PATH);

			return;

		} catch (Exception e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			logger.error("", e);
			ServletUtil.outputPlain(response, "システムでエラーが発生しました");
		} finally {
			if (executor != null) executor.finalize();
		}
	}
}
