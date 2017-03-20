package jp.co.hottolink.buzzfund.common.velocity.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.tools.generic.DateTool;

/**
 * <p>
 * VelocityのUtilクラス.
 * </p>
 * @author higa
 */
public class VelocityUtil {

	/**
	 * <p>
	 * デフォルトエンコーディング.
	 * </p>
	 */
	private static final String DEFAULT_ENCODINNG = "UTF-8";

	/**
	 * <p>
	 * Velocityコンテキストを取得する.
	 * </p>
	 * @return Velocityコンテキスト
	 */
	public static VelocityContext getContetxt() {
		VelocityContext context = new VelocityContext();
		context.put("date", new DateTool());
		return context;
	}

	/**
	 * <p>
	 * Velocityコンテキストを取得する.
	 * </p>
	 * @param map 出力オブジェクト
	 * @return Velocityコンテキスト
	 */
	@SuppressWarnings("unchecked")
	public static VelocityContext getContetxt(Map map) {
		VelocityContext context = new VelocityContext(map, getContetxt());
		return context;
	}

	/**
	 * <p>
	 * テンプレートに出力する.
	 * </p>
	 * @param template テンプレート
	 * @param encoding エンコーディング
	 * @param context コンテキスト
	 * @param writer ライター
	 * @throws Exception
	 */
	public static void mergeTemplate(String template, String encoding,
			VelocityContext context, Writer writer) throws Exception {

		InputStream is = VelocityUtil.class.getResourceAsStream("/velocity.properties");
		Properties properties = new Properties();
		properties.load(is);

		VelocityEngine velocity = new VelocityEngine(properties);
		velocity.init();

		velocity.mergeTemplate(template, encoding, context, writer);
		writer.flush();
		writer.close();
	}

	/**
	 * <p>
	 * テンプレートに出力する.
	 * </p>
	 * @param template テンプレート
	 * @param map 出力オブジェクト
	 * @param file 出力ファイル
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public static void mergeTemplate(String template, Map map, String file)
			throws Exception {
		VelocityContext context = getContetxt(map);
		FileOutputStream out = new FileOutputStream(file);
		OutputStreamWriter writer = new OutputStreamWriter(out, DEFAULT_ENCODINNG);
		mergeTemplate(template, DEFAULT_ENCODINNG, context, writer);
	}
}
