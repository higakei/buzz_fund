package jp.co.hottolink.buzzfund.common.parser;

import java.io.IOException;
import java.net.URLConnection;

import org.htmlparser.NodeFilter;
import org.htmlparser.Parser;
import org.htmlparser.http.ConnectionManager;
import org.htmlparser.lexer.Lexer;
import org.htmlparser.lexer.Page;
import org.htmlparser.lexer.Source;
import org.htmlparser.util.EncodingChangeException;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * <p>
 * HTMLのParserクラス.
 * </p>
 * @author higa
 */
public class HTMLParser {

	/**
	 * <p>
	 * デフォルトエンコーディング.
	 * </p>
	 */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/**
	 * <p>
	 * URLのHTMLパーサーを作成する.
	 * </p>
	 * @param url URL
	 * @return HTMLパーサー
	 * @throws ParserException 
	 */
	public static HTMLParser createParser(String url) throws ParserException {
		HTMLParser parser = new HTMLParser();
		parser.setConnection(getConnection(url));
		return parser;
	}

	/**
	 * <p>
	 * URLコネクションを取得する.
	 * </p>
	 * @param url URL
	 * @return URLコネクション
	 * @throws ParserException
	 */
	public static URLConnection getConnection(String url) throws ParserException {
		ConnectionManager manager = Parser.getConnectionManager();
		manager.setCookieProcessingEnabled(true);
		manager.setRedirectionProcessingEnabled(true);
		return manager.openConnection(url);
	}

	/**
	 * <p>
	 * Sourceからテキストを取得する.
	 * </p>
	 * @param source Source
	 * @return テキスト
	 * @throws IOException
	 */
	private static String getText(Source source) throws IOException {

		if (source == null) {
			return null;
		}

		StringBuffer buffer = new StringBuffer();
		for (source.reset();;) {
			char[] cbuf = new char[1024];
			int length = source.read(cbuf, 0, 1024);
			if (length == Source.EOF) break;
			buffer.append(cbuf, 0, length);
		}

		return buffer.toString();
	}

	/**
	 * <p>
	 * HTMLパーサー.
	 * </p>
	 */
	private Parser parser = null;

	/**
	 * <p>
	 * コンストラクター.
	 * </p>
	 * @throws ParserException 
	 */
	public HTMLParser() throws ParserException {
		parser = new Parser();
		parser.setEncoding(DEFAULT_ENCODING);
	}

	/**
	 * <p>
	 * HTMLソースを取得する.
	 * </p>
	 * @return HTMLソース
	 * @throws IOException
	 */
	public String getSource() throws IOException {

		Lexer lexer = parser.getLexer();
		if (lexer == null) {
			return null;
		}

		Page page = lexer.getPage();
		if (page == null) {
			return null;
		}

		return getText(page.getSource());
	}

	/**
	 * <p>
	 * 指定した文字コードで、HTMLソースを取得する.
	 * </p>
	 * @param encoding 文字コード
	 * @return HTMLソース
	 * @throws ParserException
	 * @throws IOException
	 */
	public String getSource(String encoding) throws ParserException, IOException {

		Lexer lexer = parser.getLexer();
		if (lexer == null) {
			return null;
		}

		Page page = lexer.getPage();
		if (page == null) {
			return null;
		}

		Source source = page.getSource();
		if (source == null) {
			return null;
		}

		source.setEncoding(encoding);
		return getText(source);
	}

	/**
	 * <p>
	 * HTMLを解析する.
	 * </p>
	 * @param filter ノードフィルター
	 * @return ノードリスト
	 * @throws ParserException
	 * @throws IOException 
	 * @see org.htmlparser.Parser#parse(org.htmlparser.NodeFilter)
	 */
	public NodeList parse(NodeFilter filter) throws ParserException, IOException {

		try {
			return parser.parse(filter);
		} catch (EncodingChangeException e) {
			String html = getSource();
			parser = Parser.createParser(html, DEFAULT_ENCODING);
			return parser.parse(filter);
		}
	}

	/**
	 * <p>
	 * URLコネクションを設定する.
	 * </p>
	 * @param connection URLコネクション
	 * @throws ParserException
	 * @see org.htmlparser.Parser#setConnection(java.net.URLConnection)
	 */
	public void setConnection(URLConnection connection) throws ParserException {
		parser.setConnection(connection);
	}

	/**
	 * <p>
	 * リソースを設定する.
	 * </p>
	 * @param resource HTMLまたはURLまたはファイル
	 * @throws ParserException
	 * @see org.htmlparser.Parser#setResource(java.lang.String)
	 */
	public void setResource(String resource) throws ParserException {
		parser.setResource(resource);
	}
}
