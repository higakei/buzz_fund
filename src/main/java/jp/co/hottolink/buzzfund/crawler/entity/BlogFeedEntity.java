package jp.co.hottolink.buzzfund.crawler.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

/**
 * <p>
 * ブログフィードのEntityクラス.
 * </p>
 * @author higa
 */
public class BlogFeedEntity implements Serializable {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = 6590205143547349020L;

	/**
	 * <p>
	 * 文書ID.
	 * </p>
	 */
	private String documentId = null;

	/**
	 * <p>
	 * 著者ID.
	 * </p>
	 */
	private String authorId = null;

	/**
	 * <p>
	 * URL.
	 * </p>
	 */
	private String url = null;

	/**
	 * <p>
	 * タイトル.
	 * </p>
	 */
	private String title = null;

	/**
	 * <p>
	 * 本文.
	 * </p>
	 */
	private String body = null;

	/**
	 * <p>
	 * 公開日.
	 * </p>
	 */
	private Timestamp date = null;

	/**
	 * <p>
	 * 説明文.
	 * </p>
	 */
	private String description = null;

	/**
	 * <p>
	 * 文書IDを取得する.
	 * </p>
	 * @return 文書ID
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * <p>
	 * 文書IDを設定する.
	 * </p>
	 * @param documentId 文書ID
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * <p>
	 * 著者IDを取得する.
	 * </p>
	 * @return 著者ID
	 */
	public String getAuthorId() {
		return authorId;
	}

	/**
	 * <p>
	 * 著者IDを設定する.
	 * </p>
	 * @param authorId 著者ID
	 */
	public void setAuthorId(String authorId) {
		this.authorId = authorId;
	}

	/**
	 * <p>
	 * URLを取得する.
	 * </p>
	 * @return URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * <p>
	 * URLを設定する.
	 * </p>
	 * @param url URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * <p>
	 * タイトルを取得する.
	 * </p>
	 * @return タイトル
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * <p>
	 * タイトルを設定する.
	 * </p>
	 * @param title タイトル
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * <p>
	 * 本文を取得する.
	 * </p>
	 * @return 本文
	 */
	public String getBody() {
		return body;
	}

	/**
	 * <p>
	 * 本文を設定する.
	 * </p>
	 * @param body 本文
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * <p>
	 * 公開日を取得する.
	 * </p>
	 * @return 公開日
	 */
	public Timestamp getDate() {
		return date;
	}

	/**
	 * <p>
	 * 公開日を設定する.
	 * </p>
	 * @param date 公開日
	 */
	public void setDate(Timestamp date) {
		this.date = date;
	}

	/**
	 * <p>
	 * 公開日を設定する.
	 * </p>
	 * @param date 公開日
	 */
	public void setDate(Date date) {
		if (date == null) {
			this.date = null;
		} else {
			this.date = new Timestamp(date.getTime());
		}
	}

	/**
	 * <p>
	 * 説明文を取得する.
	 * </p>
	 * @return 説明文
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * <p>
	 * 説明文を設定する.
	 * </p>
	 * @param description 説明文
	 */
	public void setDescription(String description) {
		this.description = description;
	}
}
