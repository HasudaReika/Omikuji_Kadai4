package omikuji4;

import java.io.BufferedReader;
import java.io.FileReader;
/*
 * DBから取得、格納するクラス
 */
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Random;

import db.DBManager;

public class OmikujiDB {

	/**
	 * DBにおみくじが格納されているかチェック
	 * @return DBにおみくじが格納されている場合はfalse、そうでない場合はtrueを返す
	 * @throws ClassNotFoundException DBドライバが見つからない場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public boolean checkDB() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		boolean empty = true;

		try {
			//DBに接続
			connection = DBManager.getConnection();
			//SQL文を準備
			String sql = "SELECT COUNT (*) FROM omikuji";
			//ステートメントを作成
			preparedStatement = connection.prepareStatement(sql);
			//SQLを実行
			resultSet = preparedStatement.executeQuery();

			//1行ずつレコードを取得
			while (resultSet.next()) {
				//1列目を取得
				int count = resultSet.getInt(1);
				//countが1以上であればfalse
				if (count > 0) {
					System.out.println("テーブルにおみくじが格納されています");
					empty = false;
				} else {
					//なければtrue
					System.out.println("csvからおみくじを取り込みます");
					empty = true;
				}
			}
		} catch (SQLException e) {
			System.out.println("sqlエラー");
			e.printStackTrace();

		} finally {
			//クローズ処理
			DBManager.close(resultSet);
			DBManager.close(preparedStatement);
			DBManager.close(connection);
		}
		return empty;

	}

	/**
	 * csvからおみくじを読み取りDBに追加
	 * checkDB()がfalseだった場合に呼び出されるクラス
	 * @throws ClassNotFoundException DBドライバが見つからない場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public void importOmikujiFromCsv() throws ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		//csvを読み込む
		String csv = "omikuji.csv";

		try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
			String line;
			//DBに接続
			connection = DBManager.getConnection();
			//csvを1行ずつ読み込む
			while ((line = br.readLine()) != null) {
				//「,」で区切る
				String[] values = line.split(",");
				String fortuneName = values[0];
				String negaigoto = values[1];
				String akinai = values[2];
				String gakumon = values[3];

				//SQL文の準備
				//おみくじをテーブルに追加
				String sql = "INSERT INTO omikuji values" +
						"(nextval('seq_omikuji_code'),?,?, ?, ?, '蓮田', current_date, '蓮田', current_date)";

				//fortuneNameの値によってfortuneCodeの値を決定
				int fortuneCode = 0;
				switch (fortuneName) {
				case "大吉":
					fortuneCode = 1;
					break;
				case "中吉":
					fortuneCode = 2;
					break;
				case "小吉":
					fortuneCode = 3;
					break;
				case "末吉":
					fortuneCode = 4;
					break;
				case "吉":
					fortuneCode = 5;
					break;
				case "凶":
					fortuneCode = 6;
					break;

				default:
					break;
				}

				//ステートメントを作成
				preparedStatement = connection.prepareStatement(sql);
				//入力値をバインド
				preparedStatement.setInt(1, fortuneCode);
				preparedStatement.setString(2, negaigoto);
				preparedStatement.setString(3, akinai);
				preparedStatement.setString(4, gakumon);
				//SQLを実行
				preparedStatement.executeUpdate();

			}
		} catch (Exception e) {
			e.printStackTrace();

		} finally {
			//クローズ処理
			DBManager.close(resultSet);
			DBManager.close(preparedStatement);
			DBManager.close(connection);
		}

	}

	/**
	 * 占った日と誕生日が一致する結果がテーブルに存在するかチェック
	 * テーブルに存在する場合はそのおみくじ結果を取得
	 * @param fortuneTellingDate 占った日
	 * @param birthdayDate 入力された誕生日文字列
	 * @return テーブルに一致する結果があった場合はおみくじオブジェクトを作成し返す
	 * 			なかった場合はnullを返す
	 * @throws ClassNotFoundException DBドライバが見つからなかった場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public Omikuji getOmikujiFromResult(LocalDate fortuneTellingDate, LocalDate birthdayDate)
			throws ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		try {
			//DBに接続
			connection = DBManager.getConnection();
			//SQL文の準備
			//resultテーブルに占った日と誕生日が一致する占い結果があるかチェック
			String sql = "SELECT omikuji.omikuji_code, fortune_master.fortune_name, omikuji.negaigoto, omikuji.akinai, omikuji.gakumon "
					+
					"FROM result " +
					"JOIN omikuji ON result.omikuji_code = omikuji.omikuji_code " +
					"JOIN fortune_master ON omikuji.fortune_code = fortune_master.fortune_code " +
					"WHERE result.fortune_telling_date = ? AND result.birthday = ?";

			//ステートメントを作成
			preparedStatement = connection.prepareStatement(sql);

			//LocalDate型からsqlDate型に変換
			Date ftdDate = Date.valueOf(fortuneTellingDate);
			Date birthday = Date.valueOf(birthdayDate);
			//入力値をバインド
			preparedStatement.setDate(1, ftdDate);
			preparedStatement.setDate(2, birthday);
			//SQLを実行
			resultSet = preparedStatement.executeQuery();
			//一致する結果をレコードを取得し、各値を変数に代入
			if (resultSet.next()) {
				int omikujiCode = resultSet.getInt("omikuji_code");
				String fortuneName = resultSet.getString("fortune_name");
				String negaigoto = resultSet.getString("negaigoto");
				String akinai = resultSet.getString("akinai");
				String gakumon = resultSet.getString("gakumon");

				//おみくじオブジェクトを作成
				return new Omikuji(omikujiCode, fortuneName, negaigoto, akinai, gakumon);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//クローズ処理
			DBManager.close(resultSet);
			DBManager.close(preparedStatement);
			DBManager.close(connection);
		}

		return null;

	}

	/**
	 * DBからおみくじをランダムに1つ取得
	 * getOmikujiFortuneResult()でnullが返った場合に呼び出される
	 * @return 取得したおみくじを返す
	 * @throws SQLException DB操作中にエラーが発生した場合
	 * @throws ClassNotFoundException DBドライバが見つからなかった場合
	 */
	public Omikuji getRandomOmikuji() throws SQLException, ClassNotFoundException {
		Connection connection = null;
		Omikuji randomOikuji = null;
		//omikujiテーブルのレコード数を表す変数
		int omikujiCount = 0;

		try {
			connection = DBManager.getConnection();
			//おみくじの件数を取得
			try {
				PreparedStatement preparedStatement = null;
				ResultSet resultSet = null;
				//omikujiテーブルのレコード数を取得
				String sqlCountOmikuji = "SELECT COUNT(*) FROM omikuji";
				//ステートメントを作成
				preparedStatement = connection.prepareStatement(sqlCountOmikuji);
				//SQLを実行
				resultSet = preparedStatement.executeQuery();

				//結果を取得し変数に代入
				if (resultSet.next()) {
					//レコード数をomikujiCountに代入
					omikujiCount = resultSet.getInt(1);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			try {
				PreparedStatement preparedStatement2 = null;
				ResultSet resultSet2 = null;

				//omikujiCountからランダムに数字を取得し、おみくじコードとする
				Random random = new Random();
				int omikujiCode = random.nextInt(omikujiCount);

				//おみくじコードの運勢名、願い事、商い、学問を取得
				String sqlGetOmikuji = "SELECT omikuji_code, fortune_name, negaigoto, akinai, gakumon " +
						"FROM fortune_master f LEFT OUTER JOIN omikuji o " +
						"ON f.fortune_code = o.fortune_code WHERE omikuji_code = ?";

				//ステートメントの作成
				preparedStatement2 = connection.prepareStatement(sqlGetOmikuji);
				//入力値をバインド
				preparedStatement2.setInt(1, omikujiCode);
				//SQLを実行
				resultSet2 = preparedStatement2.executeQuery();

				//結果を取得
				if (resultSet2.next()) {
					String fortuneName = resultSet2.getString("fortune_name");
					String negaigoto = resultSet2.getString("negaigoto");
					String akinai = resultSet2.getString("akinai");
					String gakumon = resultSet2.getString("gakumon");

					//おみくじオブジェクトを作成
					randomOikuji = new Omikuji(omikujiCode, fortuneName, negaigoto, akinai, gakumon);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			//クローズ処理
			DBManager.close(connection);
		}
		return randomOikuji;
	}

	/**
	 * 結果をDBに登録
	 * 新しくおみくじを生成した場合に呼び出される
	 * @param birthday 入力された誕生日文字列
	 * @param omikuji 取得したおみくじ
	 * @throws ClassNotFoundException DBドライバが見つからなかった場合
	 * @throws SQLException DB操作中にエラーが発生した場合
	 */
	public void saveResult(LocalDate birthday, Omikuji omikuji) throws ClassNotFoundException, SQLException {
		Connection connection = null;
		PreparedStatement preparedStatement = null;

		try {
			//DBに接続
			connection = DBManager.getConnection();

			//SQLを準備
			String sql = "insert into result values"
					+ "(current_date, ?, ?, '蓮田', current_date, '蓮田', current_date)";

			//ステートメントを作成
			preparedStatement = connection.prepareStatement(sql);

			//LocalDate型からsqlDate型に変換
			Date bdDate = Date.valueOf(birthday);
			//入力値をバインド
			preparedStatement.setDate(1, bdDate);
			preparedStatement.setInt(2, omikuji.getOmikujiCode());

			//sql文を実行
			preparedStatement.executeUpdate();
			System.out.println("結果をdbに登録しました");

		} catch (SQLException e) {
			System.out.println("結果を登録できませんでした");
			e.printStackTrace();
		} finally {
			//クローズ処理
			DBManager.close(preparedStatement);
			DBManager.close(connection);
		}

	}
}