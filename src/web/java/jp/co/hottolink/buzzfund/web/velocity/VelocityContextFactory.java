package jp.co.hottolink.buzzfund.web.velocity;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.tools.generic.DateTool;

/**
 * <p>
 * Velocityコンテキストのファクトリクラス.
 * </p>
 * @author higa
 */
public class VelocityContextFactory {

	/**
	 * <p>
	 * Velocityコンテキストを取得する.
	 * </p>
	 * @return Velocityコンテキスト
	 */
	public static VelocityContext getInstance() {
		VelocityContext context = new VelocityContext();
		context.put("date", new DateTool());
		return context;
	}
}
