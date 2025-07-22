package omikuji4;

/*
 * メインクラス
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OmikujiProgram {

	static OmikujiDB omikujiDB = new OmikujiDB();

	/**
	 * メインメソッド
	 * 誕生日の入力を受付、その情報に基づいておみくじ結果を表示し、DBに記録する一連の処理を実行
	 * @param args
	 * @throws IOException 入出力操作中にエラーが発生した場合
	 * @throws ClassNotFoundException DBドライバが見つからない場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public static void main(String[] args) throws IOException, ClassNotFoundException, SQLException {
		//DBにおみくじが格納されているかチェック
		if (omikujiDB.checkDB()) { //DBにおみくじが格納されていない場合
			//おみくじをDBに格納する
			omikujiDB.importOmikujiFromCsv();
		}

		//誕生日入力に進む
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String inputDateString;

		//おみくじの結果が出たらまた誕生日入力に戻る
		while (true) {

			//誕生日を入力してもらう
			System.out.println("誕生日を入力してください　例:20011009");
			//入力された日付をinputDateStringに代入
			inputDateString = br.readLine();
			if (!isValidDate(inputDateString)) {
				continue;
			}
			fortuneTelling(inputDateString);

			System.out.println("\n" + "おみくじを続けますか？　yes/no");
			BufferedReader br2 = new BufferedReader(new InputStreamReader(System.in));
			String answer = br2.readLine();
			if (answer.equals("yes")) {
				continue;
			} else {
				System.out.println("おみくじプログラムを終了します");
				break;
			}
		}
	}

	/**
	 * 入力された誕生日が正しい形式かチェック
	 * @param inputDateString 入力された誕生日の文字列'yyyyMMdd'
	 * @return 日付が正しい場合はtrue、そうでない場合はfalseを返す
	 */
	public static boolean isValidDate(String inputDateString) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		//日時解析を厳密に行う
		sdf.setLenient(false);
		//入力された日付が正しいかチェック
		try {
			sdf.parse(inputDateString);
			//正しかったらおみくじを出す
			return true;

		} catch (ParseException e) {
			//存在しない日付の場合、メッセージを出力
			System.out.println("正しい日付を入力してください");
			return false;

		}

	}

	/**
	 * 入力された誕生日に基づいておみくじの結果を取得・表示・DBに登録
	 * 占った日付と誕生日が同じ結果があればそれを表示、なければ新しく取得
	 * @param birthday 入力された誕生日文字列'yyyyMMdd'
	 * @throws ClassNotFoundException DBドライバが見つからない場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public static void fortuneTelling(String birthday) throws ClassNotFoundException, SQLException {
		//今日の日時を取得
		LocalDate today = LocalDate.now();
		//birthdayをLocalDate型に変換
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		//誕生日文字列を半角に変換
		String birthdayString = Normalizer.normalize(birthday, Form.NFKC);
		LocalDate bdDate = LocalDate.parse(birthdayString, formatter);

		Omikuji omikuji = null;
		Omikuji newOmikuji = null;
		//resultテーブルに占った日と誕生日が一致する結果が存在するかチェック、あればおみくじを取得
		//なければnullを返す
		omikuji = omikujiDB.getOmikujiFromResult(today, bdDate);
		if (omikuji != null) {
			System.out.println(omikuji.disp());

		} else {
			//DBからおみくじをランダムに1つ取得
			newOmikuji = omikujiDB.getRandomOmikuji();
			System.out.println(newOmikuji.disp());
			//結果をDBに登録
			omikujiDB.saveResult(bdDate, newOmikuji);
		}

	}

}