package jp.co.hottolink.buzzfund.calendar.tools.parser;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import jp.co.hottolink.splogfilter.common.exception.ParseException;
import jp.co.hottolink.splogfilter.common.util.DateUtil;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * <p>
 * カレンダーのSAXパーサークラス.
 * </p>
 * @author higa
 */
public class CalendarSAXParser extends DefaultHandler {

	/**
	 * <p>
	 * XMLの日付パターン.
	 * </p>
	 */
	private static final String DATE_PATTERN = "yyyy-MM-dd";

	/**
	 * <p>
	 * 東証カレンダー.
	 * </p>
	 */
	private List<Map<String, Object>> calendar = null;

	/**
	 * <p>
	 * 入力ストリームをXMLとして構文解析します.
	 * </p>
	 * @param stream 入力ストリーム
	 * @throws ParseException
	 */
	public void parse(InputStream stream) throws ParseException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(stream, this);
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	/**
	 * <p>
	 * 入力ソースをXMLとして構文解析します.
	 * </p>
	 * @param source 入力ソース
	 * @throws ParseException
	 */
	public void parse(InputSource source) throws ParseException {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			parser.parse(source, this);
		} catch (Exception e) {
			throw new ParseException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startDocument()
	 */
	@Override
	public void startDocument() throws SAXException {
		calendar = new ArrayList<Map<String, Object>>();
	}

	
	/* (non-Javadoc)
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String, java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if ("day".equals(qName)) {
			Map<String, Object> day = getAttributes(attributes);
			calendar.add(day);
		}
	}

	/**
	 * <p>
	 * 東証カレンダーを取得する.
	 * </p>
	 * @return calendar
	 */
	public List<Map<String, Object>> getCalendar() {
		return calendar;
	}

	/**
	 * <p>
	 * 属性を取得する.
	 * </p>
	 * @param attributes 属性
	 * @return 属性
	 */
	private Map<String, Object> getAttributes(Attributes attributes) {
		try {
			Map<String, Object> map = new HashMap<String, Object>();
			if (attributes == null) {
				return map;
			}

			for (int i = 0; i < attributes.getLength(); i++) {
				String key = attributes.getQName(i);
				String value = attributes.getValue(i);
				if ("date".equals(key)) {
					map.put(key, DateUtil.parseToDate(value, DATE_PATTERN));
				} else if ("day_of_week".equals(key)) {
					map.put(key, value);
				} else if ("isOpen".equals(key)) {
					map.put(key, Boolean.valueOf(value));
				} else if ("holiday".equals(key)) {
					map.put(key, value);
				}
			}

			return map;

		} catch (Exception e) {
			throw new ParseException(e);
		}
	}
}
