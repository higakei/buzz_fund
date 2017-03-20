package jp.co.hottolink.buzzfund.web.servlet.db;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import jp.co.hottolink.splogfilter.common.db.SQLExecutor;

/**
 * <p>
 * JDBCデータソースのファクトリクラス.
 * </p>
 * @author higa
 */
public class JDBCFactory {

	/**
	 * <p>
	 * JNDIルックアップ名.
	 * </p>
	 */
	private static final String JNDI = "java:comp/env/jdbc/buzz_fund";

	/**
	 * <p>
	 * データソース.
	 * </p>
	 */
	private static DataSource ds = null;

	/**
	 * <p>
	 * データソースを作成する.
	 * </p>
	 * @throws NamingException
	 */
	public static void getInstance() throws NamingException {
		InitialContext context = new InitialContext();
		ds = (DataSource)context.lookup(JNDI);
	}

	/**
	 * <p>
	 * コネクションを取得する.
	 * </p>
	 * @return
	 * @throws SQLException
	 */
	public static Connection getConnection() throws SQLException {
		if (ds == null) {
			return null;
		} else {
			return ds.getConnection();
		}
	}

	/**
	 * <p>
	 * SQL実行クラスを取得する.
	 * </p>
	 * @return SQL実行クラス
	 * @throws SQLException
	 */
	public static SQLExecutor getExecutor() throws SQLException {
		Connection conn = getConnection();
		SQLExecutor executor = new SQLExecutor(conn);
		executor.setCreate(true);
		return executor;
	}
}
