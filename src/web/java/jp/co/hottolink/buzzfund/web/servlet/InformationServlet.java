package jp.co.hottolink.buzzfund.web.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import jp.co.hottolink.buzzfund.calendar.util.ToushiCalendarUtil;
import jp.co.hottolink.buzzfund.web.model.FibonacciRetracementModel;
import jp.co.hottolink.buzzfund.web.model.PredicitionModel;
import jp.co.hottolink.buzzfund.web.servlet.db.JDBCFactory;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * 情報提供ページのサーブレットクラス.
 * </p>
 * @author higa
 */
public class InformationServlet extends HttpServlet {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = 5432113597346951092L;

	/**
	 * <p>
	 * 遷移先.
	 * </p>
	 */
	private static final String FORWARD = "/WEB-INF/jsp/information.jsp";

	/**
	 * <p>
	 * 節目を予測するデフォルト日数.
	 * </p>
	 */
	private static final int DEFAULT_RETRACEMENT_DAYS = 3;

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private Logger logger = Logger.getLogger(InformationServlet.class);

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		this.doPost(request, response);
	}

	/* (非 Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		SQLExecutor executor = null;

		try {
			// システム日付の取得
			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();

			// 株価指数
			executor = JDBCFactory.getExecutor();
			PredicitionModel pModel = new PredicitionModel(executor);
			Map<String, Object> prediction = pModel.getLatest(date);

			// 節目予測
			Map<String, Date> retracementPeriod = getDefaultRetracementPeriod(executor, date);
			FibonacciRetracementModel frModel = new FibonacciRetracementModel(executor);
			Map<String, Object> retracement = frModel.get(retracementPeriod);

			request.setAttribute("prediction", prediction);
			request.setAttribute("retracementPeriod", retracementPeriod);
			request.setAttribute("retracement", retracement);
			request.getRequestDispatcher(FORWARD).forward(request, response);

		} catch (Exception e) {
			logger.error("", e);
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		} finally {
			if (executor != null) executor.finalize();
		}
	}

	/**
	 * <p>
	 * 節目を予測をするデフォルト期間を求める.
	 * </p>
	 * @param date システム日付
	 * @return デフォルト期間
	 * @throws ParseException 
	 */
	private Map<String, Date> getDefaultRetracementPeriod(SQLExecutor executor, Date date)
			throws ParseException {
		return ToushiCalendarUtil.getPreOpenPeriod(executor, date, DEFAULT_RETRACEMENT_DAYS);
	}
}
