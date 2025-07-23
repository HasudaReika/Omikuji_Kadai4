package omikuji4;

import java.io.Serializable;

/*
 * 運勢クラス
 */

public class Omikuji implements Fortune, Serializable {

	protected int omikujiCode;

	public int getOmikujiCode() {
		return omikujiCode;
	}

	public String getFortuneName() {
		return fortuneName;
	}

	public String getNegaigoto() {
		return negaigoto;
	}

	public String getAkinai() {
		return akinai;
	}

	public String getGakumon() {
		return gakumon;
	}

	protected String fortuneName;
	protected String negaigoto;
	protected String akinai;
	protected String gakumon;

	//コンストラクタで値を取得
	public Omikuji(int omikujiCode, String fortuneName, String negaigoto, String akinai,
			String gakumon) {
		this.omikujiCode = omikujiCode;
		this.fortuneName = fortuneName;
		this.negaigoto = negaigoto;
		this.akinai = akinai;
		this.gakumon = gakumon;
	}

	@Override
	public String disp() {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format(DISP_STR, this.fortuneName)).append("\n");
		builder.append("願い事：").append(this.negaigoto).append("\n");
		builder.append("商い：").append(this.akinai).append("\n");
		builder.append("学問：").append(this.gakumon);
		return builder.toString();
	}

}
