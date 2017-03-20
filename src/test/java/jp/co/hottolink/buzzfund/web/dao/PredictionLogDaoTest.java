package jp.co.hottolink.buzzfund.web.dao;

import java.util.Calendar;
import java.util.Date;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

/**
 * <p>
 * PredictionLogDaoのテストクラス.
 * </p>
 * @author higa
 *
 */
public class PredictionLogDaoTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		SQLExecutor executor = null;

		try {
			executor = new SQLExecutor("buzz_fund_db");
			PredictionLogDao dao = new PredictionLogDao(executor);

			Calendar calendar = Calendar.getInstance();
			Date date = calendar.getTime();
			System.out.println(dao.selectByPkey(date));

			String sDate = DateUtil.toString(date, "yyyy-M-d");
			System.out.println(dao.selectByPkey(sDate));

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (executor != null) executor.finalize();
		}
	}

}
