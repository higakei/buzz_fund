package jp.co.hottolink.buzzfund.web.servlet;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;

import jp.co.hottolink.buzzfund.calendar.util.ToushiCalendarUtil;
import jp.co.hottolink.buzzfund.web.model.FibonacciRetracementModel;
import jp.co.hottolink.buzzfund.web.servlet.db.JDBCFactory;
import jp.co.hottolink.buzzfund.web.servlet.util.ServletUtil;
import jp.co.hottolink.buzzfund.web.velocity.VelocityContextFactory;
import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * フィボナッチリトレースメントを表示するサーブレットクラス.
 * </p>
 * @author higa
 */
public class FibonacciRetracementServlet extends HttpServlet {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = -7537364322453392667L;

	/**
	 * <p>
	 * テンプレートパス.
	 * </p>
	 */
	private static final String TEMPLATE_PATH = "retracement.vm";

	/**
	 * <p>
	 * ロガー.
	 * </p>
	 */
	private Logger logger = Logger.getLogger(FibonacciRetracementServlet.class);

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
			String pFrom = request.getParameter("from");
			String pTo = request.getParameter("to");

			
			// バリデーション
			executor = JDBCFactory.getExecutor();
			if (!DateUtil.validate(pFrom)) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "fromは日付ではありません");
				return;
			} else if (!DateUtil.validate(pTo)) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "toは日付ではありません");
				return;
			}

			// 営業日チェック
			Date from = DateUtil.parseToDate(pFrom);
			Date to = DateUtil.parseToDate(pTo);
			if (!isOpen(executor, from, to)) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "指定した期間は営業していません");
				return;
			}

			// 開始終了チェック
			if (from.after(to)) {
				Date tmp = from;
				from = to;
				to = tmp;
			}

			// フィボナッチリトレースメントの取得
			FibonacciRetracementModel model = new FibonacciRetracementModel(executor);
			Map<String, Object> retracement = model.get(from, to);
			if (retracement == null) {
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				ServletUtil.outputPlain(response, "指定した期間のデータは見つかりません");
				return;
			}

			// テキスト出力
			VelocityContext context = VelocityContextFactory.getInstance();
			context.put("retracement", retracement);
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

	/**
	 * <p>
	 * 指定した期間の営業状態を判定する.
	 * </p>
	 * @param executor SQL実行クラス
	 * @param from 開始日
	 * @param to 終了日
	 * @return <code>true</code>:１日は営業している, <code>false</code>:１日も営業していない
	 * @throws ParseException
	 */
	private static boolean isOpen(SQLExecutor executor, Date from, Date to)
			throws ParseException {

		int start = DateUtil.getOffsetDay(from);
		int end = DateUtil.getOffsetDay(to);
		if (start > end) {
			int tmp = start;
			start = end;
			end = tmp;
		}

		for (int i = start; i <= end; i++) {
			Date date = DateUtil.getOffsetDate(i);
			if (ToushiCalendarUtil.isOpen(executor, date)) {
				return true;
			}
		}

		return false;
	}
}
