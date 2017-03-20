package jp.co.hottolink.buzzfund.reputation.entity;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

/**
 * <p>
 * 評判情報のEntityクラス.
 * </p>
 * @author higa
 */
public class ReputationCountEntity implements Serializable {

	/**
	 * <p>
	 * serialVersionUID.
	 * </p>
	 */
	private static final long serialVersionUID = 5762230581936387718L;

	/**
	 * <p>
	 * 評判情報ID.
	 * </p>
	 */
	private int id = 0;

	/**
	 * <p>
	 * 日付.
	 * </p>
	 */
	private Date date = null;

	/**
	 * <p>
	 * 単語.
	 * </p>
	 */
	private String word = null;

	/**
	 * <p>
	 * splogフィルタ.
	 * </p>
	 */
	private Integer splog = null;

	/**
	 * <p>
	 * ポジティブ.
	 * </p>
	 */
	private int positive = 0;

	/**
	 * <p>
	 * 中立.
	 * </p>
	 */
	private int flat = 0;

	/**
	 * <p>
	 * ネガティブ.
	 * </p>
	 */
	private int negative = 0;

	/**
	 * <p>
	 * 全て.
	 * </p>
	 */
	private int total = 0;

	/**
	 * <p>
	 * 結果ステータス.
	 * </p>
	 */
	private String resultStatus = null;

	/**
	 * <p>
	 * 作成日時.
	 * </p>
	 */
	private Timestamp creationDate = null;

	/**
	 * <p>
	 * 日付を取得する.
	 * </p>
	 * @return 日付
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * <p>
	 * 日付を設定する.
	 * </p>
	 * @param date 日付
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * <p>
	 * 日付を設定する.
	 * </p>
	 * @param date 日付
	 */
	public void setDate(java.util.Date date) {
		if (date == null) {
			this.date = null;
		} else {
			this.date = new Date(date.getTime());
		}
	}

	/**
	 * <p>
	 * 単語を取得する.
	 * </p>
	 * @return 単語
	 */
	public String getWord() {
		return word;
	}

	/**
	 * <p>
	 * 単語を設定する.
	 * </p>
	 * @param word 単語
	 */
	public void setWord(String word) {
		this.word = word;
	}

	/**
	 * <p>
	 * ポジティブを取得する.
	 * </p>
	 * @return ポジティブ
	 */
	public int getPositive() {
		return positive;
	}

	/**
	 * <p>
	 * ポジティブを設定する.
	 * </p>
	 * @param positive ポジティブ
	 */
	public void setPositive(int positive) {
		this.positive = positive;
	}

	/**
	 * <p>
	 * 中立を取得する.
	 * </p>
	 * @return 中立
	 */
	public int getFlat() {
		return flat;
	}

	/**
	 * <p>
	 * 中立を設定する.
	 * </p>
	 * @param flat 中立
	 */
	public void setFlat(int flat) {
		this.flat = flat;
	}

	/**
	 * <p>
	 * ネガティブを取得する.
	 * </p>
	 * @return ネガティブ
	 */
	public int getNegative() {
		return negative;
	}

	/**
	 * <p>
	 * ネガティブを設定する.
	 * </p>
	 * @param negative ネガティブ
	 */
	public void setNegative(int negative) {
		this.negative = negative;
	}

	/**
	 * <p>
	 * 結果ステータスを取得する.
	 * </p>
	 * @return 結果ステータス
	 */
	public String getResultStatus() {
		return resultStatus;
	}

	/**
	 * <p>
	 * 結果ステータスを設定する.
	 * </p>
	 * @param resultStatus 結果ステータス
	 */
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}

	/**
	 * <p>
	 * 作成日時を取得する.
	 * </p>
	 * @return 作成日時
	 */
	public Timestamp getCreationDate() {
		return creationDate;
	}

	/**
	 * <p>
	 * 作成日時を設定する.
	 * </p>
	 * @param creationDate 作成日時
	 */
	public void setCreationDate(Timestamp creationDate) {
		this.creationDate = creationDate;
	}

	/**
	 * <p>
	 * 評判情報IDを取得する.
	 * </p>
	 * @return 評判情報ID
	 */
	public int getId() {
		return id;
	}

	/**
	 * <p>
	 * 評判情報IDを設定する.
	 * </p>
	 * @param id 評判情報ID
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * <p>
	 * splogフィルタを取得する.
	 * </p>
	 * @return splogフィルタ
	 */
	public Integer getSplog() {
		return splog;
	}

	/**
	 * <p>
	 * splogフィルタを設定する.
	 * </p>
	 * @param splog splogフィルタ
	 */
	public void setSplog(Integer splog) {
		this.splog = splog;
	}

	/**
	 * <p>
	 * 全てを取得する.
	 * </p>
	 * @return 全て
	 */
	public int getTotal() {
		return total;
	}

	/**
	 * <p>
	 * 全てを設定する.
	 * </p>
	 * @param total 全て
	 */
	public void setTotal(int total) {
		this.total = total;
	}
}
