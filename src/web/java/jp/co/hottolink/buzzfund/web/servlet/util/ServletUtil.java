package jp.co.hottolink.buzzfund.web.servlet.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

/**
 * <p>
 * サーブレットのUtilクラス.
 * </p>
 * @author higa
 *
 */
public class ServletUtil {

	/**
	 * <p>
	 * デフォルトエンコーディング.
	 * </p>
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * <p>
	 * XMLのコンテントタイプ.
	 * </p>
	 */
	private static final String CONTENT_TYPE_PLAIN = "text/plain";

	/**
	 * <p>
	 * テキストを出力する.
	 * </p>
	 * @param response レスポンス
	 * @param text テキスト
	 * @throws IOException
	 */
	public static void outputPlain(HttpServletResponse response, String text)
			throws IOException {

		response.setContentType(CONTENT_TYPE_PLAIN);
		response.setHeader("charset", DEFAULT_ENCODING);
		response.setCharacterEncoding(DEFAULT_ENCODING);

		PrintWriter writer = response.getWriter();
		writer.println(text);
		writer.flush();
		writer.close();
	}

	/**
	 * <p>
	 * テキストをVelocityから出力する.
	 * </p>
	 * @param response レスポンス
	 * @param context コンテキスト
	 * @param template テンプレート
	 * @throws ResourceNotFoundException
	 * @throws ParseErrorException
	 * @throws MethodInvocationException
	 * @throws Exception
	 */
	public static void outputPlain(HttpServletResponse response,
			VelocityContext context, String template)
			throws ResourceNotFoundException, ParseErrorException,
			MethodInvocationException, Exception {

		response.setContentType(CONTENT_TYPE_PLAIN);
		response.setHeader("charset", DEFAULT_ENCODING);
		response.setCharacterEncoding(DEFAULT_ENCODING);

		PrintWriter writer = response.getWriter();
		Velocity.mergeTemplate(template, DEFAULT_ENCODING, context, writer);
		writer.flush();
		writer.close();
	}
}
