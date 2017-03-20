package jp.co.hottolink.buzzfund.web.servlet.util;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * HTTPのUtilクラス.
 * </p>
 * @author higa
 */
public class HttpUtils {

	/**
	 * <p>
	 * HTTP.
	 * </p>
	 */
	private static final String SCHEME_HTTP = "http";

	/**
	 * <p>
	 * HTTPS.
	 * </p>
	 */
	private static final String SCHEME_HTTPS = "https";

	/**
	 * <p>
	 * ポート8080.
	 * </p><pre>
	 * Tomcatの非SSLポート
	 * </pre>
	 */
	private static final int PORT_8080 = 8080;

	/**
	 * <p>
	 * ポート8443.
	 * </p><pre>
	 * TomcatのSSLポート
	 * </pre>
	 */
	private static final int PORT_8443 = 8443;
	
	/**
	 * <p>
	 * サーバーURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getServerURL(HttpServletRequest request) {

        StringBuffer url = new StringBuffer();
        String scheme = request.getScheme();
        int port = getPort(request);

        url.append(scheme);
        url.append("://");
        url.append(request.getServerName());
        if ((SCHEME_HTTP.equals(scheme) && (port != 80))
            || (SCHEME_HTTPS.equals(scheme) && (port != 443))) {
            url.append(':');
            url.append(port);
        }

		return url.toString();
	}

	/**
	 * <p>
	 * SSLのサーバーURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getHttpsServerURL(HttpServletRequest request) {

		// HTTP以外
		String url = getServerURL(request);
		String scheme = request.getScheme();
		if (!SCHEME_HTTP.equals(scheme)) return url;
		
		// HTTP→HTTPS
		url = url.replaceFirst(SCHEME_HTTP, SCHEME_HTTPS);

		// ポート8080以外
		int port = getPort(request);
		if (port != PORT_8080) return url;

		// 8080→8443
		url = url.replaceFirst(String.valueOf(PORT_8080), String.valueOf(PORT_8443));
		return url;
	}

	/**
	 * <p>
	 * 非SSLのサーバーURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getHttpServerURL(HttpServletRequest request) {

		// HTTPS以外
		String url = getServerURL(request);
		String scheme = request.getScheme();
		if (!SCHEME_HTTPS.equals(scheme)) return url;
		
		// HTTPS→HTTP
		url = url.replaceFirst(SCHEME_HTTPS, SCHEME_HTTP);

		// ポート8443以外
		int port = getPort(request);
		if (port != PORT_8443) return url;

		// 8443→8080
		url = url.replaceFirst(String.valueOf(PORT_8443), String.valueOf(PORT_8080));
		return url;
	}

	/**
	 * <p>
	 * コンテキストURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getContextURL(HttpServletRequest request) {

		StringBuffer url = new StringBuffer();
		String serverUrl = getServerURL(request);
		String contextPath = request.getContextPath();

		url.append(serverUrl);
		url.append(contextPath);

		return url.toString();
	}

	/**
	 * <p>
	 * SSLのコンテキストURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getHttpsContextURL(HttpServletRequest request) {

		StringBuffer url = new StringBuffer();
		String serverUrl = getHttpsServerURL(request);
		String contextPath = request.getContextPath();

		url.append(serverUrl);
		url.append(contextPath);

		return url.toString();
	}

	/**
	 * <p>
	 * 非SSLのコンテキストURLを取得する.
	 * </p>
	 * @param request リクエスト
	 * @return URL
	 */
	public static String getHttpContextURL(HttpServletRequest request) {

		StringBuffer url = new StringBuffer();
		String serverUrl = getHttpServerURL(request);
		String contextPath = request.getContextPath();

		url.append(serverUrl);
		url.append(contextPath);

		return url.toString();
	}

	/**
	 * <p>
	 * ポート番号を取得する.
	 * </p>
	 * @param request リクエスト
	 * @return ポート番号
	 */
	private static int getPort(HttpServletRequest request) {
        int port = request.getServerPort();
        if (port < 0) port = 80; // Work around java.net.URL bug
		return port;
	}
}
